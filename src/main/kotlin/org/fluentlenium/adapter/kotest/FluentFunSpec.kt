package org.fluentlenium.adapter.kotest

import io.kotest.core.spec.style.FunSpec
import org.fluentlenium.core.FluentControl

abstract class FluentFunSpec internal constructor(private val koTestAdapter: KoTestAdapter,
                                                  body: FluentFunSpec.() -> Unit = {}) : FunSpec({ }), FluentControl by koTestAdapter {

    constructor(body: FluentFunSpec.() -> Unit = {}) : this(KoTestAdapter(), body)

    init {
        listener(koTestAdapter.listener())

        body()
    }
}