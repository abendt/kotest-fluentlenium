package org.fluentlenium.adapter.kotest

import io.github.bonigarcia.wdm.WebDriverManager
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.string.shouldContain

class HelloKoTestSpec : DescribeSpec() {

    init {
        val fluentlenium = withFluentlenium(initWebDriver = {
            WebDriverManager.chromedriver().setup()
        })

        it("Title of duck duck go") {
            fluentlenium {
                goTo("https://duckduckgo.com")

                jq("#search_form_input_homepage").fill().with("FluentLenium")
                jq("#search_button_homepage").submit()

                window().title() shouldContain "FluentLenium"
            }
        }
    }
}