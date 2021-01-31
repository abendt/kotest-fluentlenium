package org.fluentlenium.adapter.kotest.describespec

import io.kotest.matchers.shouldBe
import org.fluentlenium.adapter.kotest.FluentDescribeSpec
import org.fluentlenium.configuration.ConfigurationProperties
import org.fluentlenium.configuration.FluentConfiguration

@FluentConfiguration(driverLifecycle = ConfigurationProperties.DriverLifecycle.JVM,
        configurationDefaults = CustomConfigurationDefault::class)
class ConfigurationDefaultSpec : FluentDescribeSpec(
        {
            it("remoteUrl via custom default class") {
                remoteUrl shouldBe "https://www.google.com"
            }
        }
)