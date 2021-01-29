package org.fluentlenium.adapter.kotest

import io.appium.java_client.MobileBy
import io.kotest.core.test.TestContext
import org.fluentlenium.adapter.FluentAdapter
import org.fluentlenium.adapter.FluentStandaloneRunnable
import org.fluentlenium.core.FluentControl
import org.fluentlenium.core.domain.FluentList
import org.fluentlenium.core.domain.FluentWebElement
import org.fluentlenium.core.search.SearchFilter
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

interface KoFluentTestContext : TestContext, FluentControl {
    fun jq(selector: String, vararg filter: SearchFilter): FluentList<FluentWebElement> = `$`(selector, *filter)

    fun jq(rawElements: MutableList<WebElement>): FluentList<FluentWebElement> = `$`(rawElements)

    fun jq(vararg filters: SearchFilter): FluentList<FluentWebElement> = `$`(*filters)

    fun jq(locator: By, vararg filters: SearchFilter): FluentList<FluentWebElement> = `$`(locator, *filters)

    fun jq(locator: MobileBy, vararg filters: SearchFilter): FluentList<FluentWebElement> = `$`(locator, *filters)
}

typealias KoFluentTest = KoFluentTestContext.() -> Unit

typealias KoFluentTestWithContext = TestContext.(KoFluentTest) -> Unit

fun withFluentlenium(initWebDriver: () -> Unit,
                     onBefore: FluentAdapter.() -> Unit = {},
                     onSuccess: FluentAdapter.() -> Unit = {},
                     onError: FluentAdapter.(Exception) -> Unit = { throw it }): KoFluentTestWithContext =
        { block ->
            initWebDriver()

            val testContext = this
            object : FluentStandaloneRunnable() {
                override fun doRun() {
                    val receiver = DelegatingContext(testContext, this)

                    onBefore()

                    try {
                        receiver.block()
                        onSuccess()
                    } catch (e: Exception) {
                        onError(e)
                    }
                }
            }.run()
        }

private class DelegatingContext(private val testContext: TestContext,
                                private val fluentControl: FluentControl) :
        KoFluentTestContext,
        TestContext by testContext,
        FluentControl by fluentControl