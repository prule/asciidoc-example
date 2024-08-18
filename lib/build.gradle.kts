import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.asciidoctor.gradle.jvm.pdf.AsciidoctorPdfTask

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.jvm)

    // Apply the java-library plugin for API and implementation separation.
    `java-library`

    alias(libs.plugins.asciidoctor.pdf)
    alias(libs.plugins.asciidoctor.convert)
    alias(libs.plugins.asciidoctor.epub)
    alias(libs.plugins.asciidoctor.gems)
}

repositories {
    mavenCentral()
    ruby {
        gems()
    }
}

tasks.register("generateDocs") {
    dependsOn("asciidoctor", "asciidoctorPdf")
    group = "documentation"
    description = "Generates both HTML and PDF documentation"
}

// tag::asciidoctor-gradle-configuration[]
tasks {

    val asciidocAttributes = mapOf(
        // define a custom attribute to be used in the document eg as {source}
        // unfortunately these won't work in the intellij preview, only in the gradle output
        // so you would need to separately define these attributes in the intellij settings
        "source" to project.sourceSets.test.get().kotlin.srcDirs.first(),
    )

    "asciidoctor"(AsciidoctorTask::class) {
        baseDirFollowsSourceDir()
        attributes(asciidocAttributes)
    }
    "asciidoctorPdf"(AsciidoctorPdfTask::class) {
        baseDirFollowsSourceDir()
        attributes(asciidocAttributes)
    }
}
// end::asciidoctor-gradle-configuration[]

dependencies {
    // Use the Kotlin JUnit 5 integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    // Use the JUnit 5 integration.
    testImplementation(libs.junit.jupiter.engine)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // This dependency is exported to consumers, that is to say found on their compile classpath.
    api(libs.commons.math3)

    // This dependency is used internally, and not exposed to consumers on their own compile classpath.
    implementation(libs.guava)

    asciidoctorGems("rubygems:rouge:4.2.0")

}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
