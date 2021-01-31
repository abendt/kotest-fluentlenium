package org.fluentlenium.adapter.kotest.describespec

import io.github.bonigarcia.wdm.WebDriverManager
import io.kotest.core.spec.Spec
import io.kotest.matchers.shouldBe
import org.fluentlenium.adapter.kotest.FluentDescribeSpec
import org.fluentlenium.configuration.ConfigurationProperties
import org.fluentlenium.configuration.CustomProperty
import org.fluentlenium.configuration.FluentConfiguration

@FluentConfiguration(driverLifecycle = ConfigurationProperties.DriverLifecycle.JVM, custom = [CustomProperty(name = "foo", value = "bar")])
class ConfigurationSpec : FluentDescribeSpec() {

    override fun beforeSpec(spec: Spec) {
        WebDriverManager.chromedriver().setup()
    }

    override fun getBrowserTimeout(): Long = 4711

    init {
        setCustomProperty("customProp", "myValue")

        it("driverLifeCycle via annotation") {
            driverLifecycle shouldBe ConfigurationProperties.DriverLifecycle.JVM
        }

        it("custom property via annotation") {
            getCustomProperty("foo") shouldBe "bar"
        }

        it("browser timeout via property getter override") {
            browserTimeout shouldBe 4711
        }

        it("custom property via property setter") {
            getCustomProperty("customProp") shouldBe "myValue"
        }

        it("property from .properties") {
            awaitAtMost shouldBe 30001
        }
    }
}