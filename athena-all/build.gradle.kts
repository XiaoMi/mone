plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.7.20"
    id("org.jetbrains.intellij") version "1.14.0"
}

group = "run.mone"
version = "2024.01.16.1"


repositories {
    mavenLocal()
    mavenCentral()
}


// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.1.2")
    type.set("IC") // Target IDE Platform
    plugins.set(listOf("com.intellij.java"/* Plugin Dependencies */))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("222")
        untilBuild.set("300.*")
    }
    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    initializeIntelliJPlugin {
        //不关闭,在有的网络下,会特别慢
        selfUpdateCheck.set(false)
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("com.ibeetl:beetl:3.15.4.RELEASE")
    implementation("org.antlr:antlr4-runtime:4.7.2")
    implementation("io.netty:netty-all:4.1.36.Final")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("run.mone:openai:1.4-SNAPSHOT")
    implementation("com.google.guava:guava:32.0.1-jre")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:okhttp-sse:4.11.0")
    implementation("org.apache.commons:commons-lang3:3.13.0")
    implementation("com.github.javaparser:javaparser-core:3.25.6")

    implementation("org.nutz:nutz:1.r.69.20210929")
    implementation("mysql:mysql-connector-java:8.0.28")
    implementation("org.apache.commons:commons-lang3:3.14.0")

    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")

    testCompileOnly("org.projectlombok:lombok:1.18.26")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.26")

}