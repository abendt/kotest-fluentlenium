import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
    id("com.adarshr.test-logger") version "2.1.1"
    id("io.qameta.allure") version "2.8.1"
}

allure {
    version = "2.13.8"
    autoconfigure = true
    aspectjVersion = "1.9.6"
}

val isIdea = System.getProperty("idea.version") != null

testlogger {
    // idea can't handle ANSI output
    setTheme(if (isIdea) "plain" else "mocha")
    isShowFullStackTraces = false
}

repositories {
    jcenter()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        apiVersion = "1.4"
        languageVersion = "1.4"
        freeCompilerArgs = listOf("-Xjsr305=strict", "-progressive")
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()

    systemProperty(
            "fluentlenium.capabilities",
            """{"chromeOptions": {"args": ["headless","no-sandbox", "disable-gpu", "disable-dev-shm-usage"]}}"""
    )
}

dependencies {
    api("org.fluentlenium:fluentlenium-core:4.5.1")

    val kotlinTestVersion = "4.3.2"
    api("io.kotest:kotest-runner-junit5:$kotlinTestVersion")

    implementation(kotlin("stdlib-jdk8"))

    testImplementation("io.github.bonigarcia:webdrivermanager:4.3.1")
    testImplementation("org.seleniumhq.selenium:selenium-api:3.141.59")
    testImplementation("org.seleniumhq.selenium:selenium-chrome-driver:3.141.59")

    testImplementation("ch.qos.logback:logback-classic:1.2.3")
}