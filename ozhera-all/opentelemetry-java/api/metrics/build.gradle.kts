plugins {
    id("java-library")
    id("maven-publish")

    id("me.champeau.jmh")
    id("ru.vyarus.animalsniffer")
}

description = "OpenTelemetry API"
extra["moduleName"] = "io.opentelemetry.api.metrics"

dependencies {
    api(project(":api:all"))

    annotationProcessor("com.google.auto.value:auto-value")

    testImplementation("edu.berkeley.cs.jqf:jqf-fuzz")
    testImplementation("com.google.guava:guava-testlib")
}
