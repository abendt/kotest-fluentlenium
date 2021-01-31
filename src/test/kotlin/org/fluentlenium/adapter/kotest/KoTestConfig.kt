package org.fluentlenium.adapter.kotest

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.Listener
import io.kotest.core.listeners.TestListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.kotest.core.spec.Spec
import io.github.bonigarcia.wdm.WebDriverManager

class KoTestConfig : AbstractProjectConfig() {
    override fun listeners(): List<Listener> =
            listOf(object : TestListener {
                override suspend fun beforeSpec(spec: Spec) {
                    withContext(Dispatchers.IO) {
                        WebDriverManager.chromedriver().setup()
                    }
                }
            })

    override val parallelism: Int?
        get() = 2
}