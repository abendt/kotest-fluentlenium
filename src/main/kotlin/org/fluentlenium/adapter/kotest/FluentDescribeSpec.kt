package org.fluentlenium.adapter.kotest

import io.kotest.core.spec.style.DescribeSpec
import org.fluentlenium.adapter.IFluentAdapter
import org.fluentlenium.adapter.TestRunnerAdapter
import org.fluentlenium.adapter.exception.AnnotationNotFoundException
import org.fluentlenium.adapter.kotest.internal.KoTestFluentAdapter
import org.fluentlenium.configuration.Configuration
import org.fluentlenium.configuration.ConfigurationFactoryProvider

abstract class FluentDescribeSpec internal constructor(private val fluentAdapter: KoTestFluentAdapter,
                                                       body: FluentDescribeSpec.() -> Unit = {}) : DescribeSpec({ }),
        IFluentAdapter by fluentAdapter,
        TestRunnerAdapter {

    constructor(body: FluentDescribeSpec.() -> Unit = {}) : this(KoTestFluentAdapter(), body)

    init {
        fluentAdapter.useConfigurationOverride = { configuration }

        listener(fluentAdapter.listener())

        body()
    }

    override fun getConfiguration(): Configuration =
            ConfigurationFactoryProvider.newConfiguration(javaClass)

    // what is TestRunnerAdapter actually needed for?

    override fun getTestClass(): Class<*> = javaClass

    override fun getTestMethodName(): String =
            fluentAdapter.currentTestName.get()

    override fun <T : Annotation?> getClassAnnotation(annotation: Class<T>?): T =
            javaClass.getAnnotation(annotation) ?: throw AnnotationNotFoundException()

    override fun <T : Annotation?> getMethodAnnotation(annotation: Class<T>?): T {
        throw AnnotationNotFoundException()
    }
}
