import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.0"
    id("com.adarshr.test-logger") version "3.0.0"
}

val isIdea = System.getProperty("idea.version") != null

testlogger {
    // idea can't handle ANSI output
    setTheme(if (isIdea) "plain" else "mocha")

    showFullStackTraces = true
    showCauses = true
    showExceptions = true
}



repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        apiVersion = "1.5"
        languageVersion = "1.5"
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
    api("org.fluentlenium:fluentlenium-core:4.6.2")

    val kotlinTestVersion = "4.6.0"
    api("io.kotest:kotest-runner-junit5:$kotlinTestVersion")

    implementation(kotlin("stdlib-jdk8"))

    testImplementation("io.github.bonigarcia:webdrivermanager:4.4.3")
    testImplementation("org.seleniumhq.selenium:selenium-api:3.141.59")
    testImplementation("org.seleniumhq.selenium:selenium-chrome-driver:3.141.59")

    testImplementation("ch.qos.logback:logback-classic:1.2.3")
}