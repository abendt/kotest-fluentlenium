package org.fluentlenium.adapter.kotest

import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.fluentlenium.adapter.DefaultSharedMutator
import org.fluentlenium.adapter.FluentAdapter
import org.fluentlenium.adapter.TestRunnerCommon.*
import org.fluentlenium.adapter.sharedwebdriver.SharedWebDriverContainer
import org.fluentlenium.configuration.ConfigurationProperties
import org.fluentlenium.core.FluentControl
import org.fluentlenium.utils.ScreenshotUtil

internal class KoTestAdapter(val fluentAdapter: FluentAdapter = FluentAdapter()) : FluentControl by fluentAdapter {

    init {
        if (configuration.driverLifecycle == ConfigurationProperties.DriverLifecycle.THREAD) {
            throw IllegalArgumentException("driverlifecycle THREAD not supported")
        }
    }

    private val sharedMutator = DefaultSharedMutator()

    fun listener(): TestListener = object : TestListener {
        override suspend fun beforeTest(testCase: TestCase) {
            val thisListener = this
            val driver = withContext(Dispatchers.IO) {
                getTestDriver(
                        testCase.spec.javaClass,
                        testCase.displayName,
                        fluentAdapter::newWebDriver,
                        thisListener::failed,
                        configuration,
                        sharedMutator.getEffectiveParameters(testCase.spec.javaClass, testCase.displayName, driverLifecycle)
                )
            }
            fluentAdapter.initFluent(driver.driver)
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
                fluentAdapter.releaseFluent()
            }
        }

        private fun failed(error: Throwable?, testClass: Class<*>, testName: String) {
            if (fluentAdapter.isFluentControlAvailable && !ScreenshotUtil.isIgnoredException(error)) {
                doScreenshot(testClass, testName, fluentAdapter, configuration)
                doHtmlDump(testClass, testName, fluentAdapter, configuration)
            }
        }
    }
}