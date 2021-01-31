package org.fluentlenium.adapter.kotest

import io.github.bonigarcia.wdm.WebDriverManager
import io.kotest.core.spec.Spec
import io.kotest.matchers.string.shouldContain

class HelloFluentFunSpec : FluentFunSpec() {
    override fun beforeSpec(spec: Spec) {
        WebDriverManager.chromedriver().setup()
    }

    init {
        test("Title of duck duck go") {
            goTo("https://duckduckgo.com")

            jq("#search_form_input_homepage").fill().with("FluentLenium")
            jq("#search_button_homepage").submit()

            window().title() shouldContain "FluentLenium"
        }
    }
}