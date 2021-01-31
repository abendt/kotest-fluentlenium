package org.fluentlenium.adapter.kotest.annotationspec

import io.github.bonigarcia.wdm.WebDriverManager
import io.kotest.core.spec.Spec
import io.kotest.matchers.string.shouldContain
import org.fluentlenium.adapter.kotest.FluentAnnotationSpec
import org.fluentlenium.adapter.kotest.jq

class ExampleAnnotationSpec : FluentAnnotationSpec() {

    override fun beforeSpec(spec: Spec) {
        WebDriverManager.chromedriver().setup()
    }

    @Test
    fun queryDuckDuckGo() {
        goTo("https://duckduckgo.com");
        jq("#search_form_input_homepage").fill().with("FluentLenium");
        jq("#search_button_homepage").submit();
        window().title() shouldContain  "FluentLenium"
    }
}