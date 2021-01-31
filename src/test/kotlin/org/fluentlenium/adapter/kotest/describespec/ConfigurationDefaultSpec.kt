package org.fluentlenium.adapter.kotest.describespec

import io.github.bonigarcia.wdm.WebDriverManager
import io.kotest.core.spec.Spec
import io.kotest.matchers.shouldBe
import org.fluentlenium.adapter.kotest.FluentDescribeSpec
import org.fluentlenium.configuration.ConfigurationProperties
import org.fluentlenium.configuration.FluentConfiguration

@FluentConfiguration(driverLifecycle = ConfigurationProperties.DriverLifecycle.JVM, configurationDefaults = CustomConfigurationDefault::class)
class ConfigurationDefaultSpec : FluentDescribeSpec() {

    override fun beforeSpec(spec: Spec) {
        WebDriverManager.chromedriver().setup()
    }

    init {
        it("remoteUrl via custom default class") {
            remoteUrl shouldBe "https://www.google.com"
        }
    }
}