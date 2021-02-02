package org.fluentlenium.adapter.kotest.internal

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.fluentlenium.adapter.DefaultSharedMutator
import org.fluentlenium.adapter.FluentAdapter
import org.fluentlenium.adapter.FluentTestRunnerAdapter
import org.fluentlenium.adapter.IFluentAdapter
import org.fluentlenium.adapter.TestRunnerCommon.*
import org.fluentlenium.adapter.sharedwebdriver.SharedWebDriverContainer
import org.fluentlenium.configuration.Configuration
import org.fluentlenium.configuration.ConfigurationProperties
import org.fluentlenium.utils.ScreenshotUtil
import java.util.concurrent.atomic.AtomicReference

internal class KoTestFluentAdapter constructor(var useConfigurationOverride: () -> Configuration = { throw IllegalStateException() }) : IFluentAdapter, FluentAdapter() {

    val sharedMutator = DefaultSharedMutator()

    /**
     * KoTest doc states that tests inside a Spec are always executed sequentially (https://kotest.io/docs/framework/project-config.html#parallelism)
     * So it should be safe to store the current test name like this.
     */
    val currentTestName = AtomicReference<String>()

    val configurationOverride: Configuration by lazy { useConfigurationOverride() }

    override fun getConfiguration(): Configuration = configurationOverride

    fun listener(): TestListener = object : TestListener {
        override suspend fun beforeSpec(spec: Spec) {
            if (driverLifecycle == ConfigurationProperties.DriverLifecycle.THREAD) {
                // not sure about this:
                // as the tests are executed within a Coroutine it
                // could be possible that the "same" thread executes mulitple tests and/or that one test is executed
                // by mulitple Coroutines with different underlying Threads. need to investigate

                throw IllegalArgumentException("DriverLifecyle $driverLifecycle will prohably not work as expected!")
            }
        }

        override suspend fun beforeTest(testCase: TestCase) {
            val thisListener = this
            val testClass = testCase.spec.javaClass
            val testName = testCase.displayName

            currentTestName.set(testName)

            val driver = withContext(Dispatchers.IO) {
                getTestDriver(
                        testCase.spec.javaClass,
                        testName,
                        ::newWebDriver,
                        thisListener::failed,
                        configuration,
                        sharedMutator.getEffectiveParameters(testClass, testName, driverLifecycle)
                )
            }
            initFluent(driver.driver)
        }

        override suspend fun afterTest(testCase: TestCase, result: TestResult) {

            val testClass = testCase.spec.javaClass
            val testName = testCase.displayName

            withContext(Dispatchers.IO) {
                if (result.status == TestStatus.Error || result.status == TestStatus.Failure) {
                    failed(result.error, testClass, testName)
                }

                val sharedWebDriver = SharedWebDriverContainer.INSTANCE
                        .getDriver(sharedMutator.getEffectiveParameters(testClass, testName, driverLifecycle))

                quitMethodAndThreadDrivers(driverLifecycle, sharedWebDriver)
                deleteCookies(sharedWebDriver, configuration)
                releaseFluent()
            }

            currentTestName.set(null)
        }

        fun failed(error: Throwable?, testClass: Class<*>, testName: String) {
            if (isFluentControlAvailable && !ScreenshotUtil.isIgnoredException(error)) {
                doScreenshot(testClass, testName, this@KoTestFluentAdapter, configuration)
                doHtmlDump(testClass, testName, this@KoTestFluentAdapter, configuration)
            }
        }

        override suspend fun afterSpec(spec: Spec) {
            FluentTestRunnerAdapter.classDriverCleanup(spec.javaClass)
        }
    }
}