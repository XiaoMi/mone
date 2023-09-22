plugins {
    id("java-library")
    id("maven-publish")
}

description = "OpenTelemetry SDK Testing utilities"
extra["moduleName"] = "io.opentelemetry.sdk.testing"

dependencies {
    api(project(":api:all"))
    api(project(":sdk:all"))

    compileOnly("org.assertj:assertj-core")
    compileOnly("junit:junit")
    compileOnly("org.junit.jupiter:junit-jupiter-api")

    annotationProcessor("com.google.auto.value:auto-value")

    testImplementation("junit:junit")
}
