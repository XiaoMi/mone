import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    `maven-publish`

    id("me.champeau.jmh")
    id("org.jetbrains.kotlin.jvm")
    id("org.unbroken-dome.test-sets")
    id("ru.vyarus.animalsniffer")
}

description = "OpenTelemetry Kotlin Extensions"
extra["moduleName"] = "io.opentelemetry.extension.kotlin"

testSets {
    create("testStrictContext")
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    api(project(":api:all"))

    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-common")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")

    testImplementation(project(":sdk:testing"))
    testImplementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
}

tasks {
    withType(KotlinCompile::class) {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    // We don't have any public Java classes
    named("javadoc") {
        enabled = false
    }

    named<Test>("testStrictContext") {
        jvmArgs("-Dio.opentelemetry.context.enableStrictContext=true")
    }
}
