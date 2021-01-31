package org.fluentlenium.adapter.kotest

import io.kotest.core.spec.style.DescribeSpec
import org.fluentlenium.core.FluentControl

abstract class FluentDescribeSpec internal constructor(private val koTestAdapter: KoTestAdapter,
                                                       body: FluentDescribeSpec.() -> Unit = {}) : DescribeSpec({ }), FluentControl by koTestAdapter {

    constructor(body: FluentDescribeSpec.() -> Unit = {}) : this(KoTestAdapter(), body)

    init {
        listener(koTestAdapter.listener())

        body()
    }
}