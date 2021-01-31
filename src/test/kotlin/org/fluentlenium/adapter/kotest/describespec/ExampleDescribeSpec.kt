package org.fluentlenium.adapter.kotest.describespec

import io.github.bonigarcia.wdm.WebDriverManager
import io.kotest.core.spec.Spec
import io.kotest.matchers.string.shouldContain
import org.fluentlenium.adapter.kotest.FluentDescribeSpec
import org.fluentlenium.adapter.kotest.jq

class ExampleDescribeSpec : FluentDescribeSpec() {

    override fun beforeSpec(spec: Spec) {
        WebDriverManager.chromedriver().setup()
    }

    init {
        it("Title of duck duck go") {
            goTo("https://duckduckgo.com")

            jq("#search_form_input_homepage").fill().with("FluentLenium")
            jq("#search_button_homepage").submit()

            window().title() shouldContain "FluentLenium"
        }
    }
}