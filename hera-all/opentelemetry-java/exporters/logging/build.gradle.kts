plugins {
    `java-library`
    `maven-publish`

    id("ru.vyarus.animalsniffer")
}

description = "OpenTelemetry - Logging Exporter"
extra["moduleName"] = "io.opentelemetry.exporter.logging"

dependencies {
    api(project(":sdk:all"))
    api(project(":sdk:metrics"))
    implementation(project(":semconv"))
    implementation("org.apache.logging.log4j:log4j-core:2.17.0")
    implementation("org.apache.logging.log4j:log4j-api:2.17.0")
    implementation("com.lmax:disruptor:3.4.2")

    testImplementation(project(":sdk:testing"))
}
