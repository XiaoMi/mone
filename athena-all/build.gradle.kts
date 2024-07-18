import java.util.*

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.7.20"
    id("org.jetbrains.intellij") version "1.14.0"
}

group = "run.mone"

// Load properties from src/main/resources/athena.properties
val athenaProperties = Properties().apply {
    file("src/main/resources/athena.properties").inputStream().use { load(it) }
}
// Access the property
val pluginVersion = athenaProperties.getProperty("pluginVersion")

version = pluginVersion


repositories {
    mavenLocal()
    maven(uri("https://maven.aliyun.com/repository/public/"))
    mavenCentral()
}


// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.1.2")
    type.set("IC") // Target IDE Platform
    plugins.set(listOf("com.intellij.java","JUnit","org.jetbrains.plugins.terminal"/* Plugin Dependencies */))
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

    implementation("org.slf4j:slf4j-api:2.0.13")

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("com.ibeetl:beetl:3.15.4.RELEASE")

    implementation("org.antlr:antlr4-runtime:4.7.2")
    implementation("io.netty:netty-all:4.1.36.Final")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("run.mone:openai:1.4-SNAPSHOT")
    implementation("com.google.guava:guava:32.0.1-jre")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:okhttp-sse:4.10.0")
    implementation("org.apache.commons:commons-lang3:3.13.0")
    implementation("com.github.javaparser:javaparser-core:3.25.6")

    implementation("org.nutz:nutz:1.r.69.20210929")
    implementation("mysql:mysql-connector-java:8.0.28")
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("run.mone:codegen:1.5-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")

    testCompileOnly("org.projectlombok:lombok:1.18.26")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.26")

}

buildscript {
    repositories {
        mavenLocal()
        maven(uri("https://maven.aliyun.com/repository/public/"))
        mavenCentral()
    }
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.5.0")
    }
}

tasks.register<proguard.gradle.ProGuardTask>("proguard") {
    verbose()
//    keepdirectories()// By default, directory entries are removed.
    ignorewarnings()
    target("17")

    // Alternatively put your config in a separate file
//     configuration("config.pro")

    // Use the jar task output as a input jar. This will automatically add the necessary task dependency.
    injars(tasks.named("instrumentedJar"))
//    injars("build/libs/*.jar")

//    outjars("build/${rootProject.name}-obfuscated.jar")
//    injars("Athena-2024.06.03.2.jar")
    outjars("build/instrumented-${rootProject.name}-obfuscated.jar")
    val javaHome = System.getProperty("java.home")
    // Automatically handle the Java version of this build, don't support JBR
    // As of Java 9, the runtime classes are packaged in modular jmod files.
//        libraryjars(
//            // filters must be specified first, as a map
//            mapOf("jarfilter" to "!**.jar",
//                  "filter"    to "!module-info.class"),
//            "$javaHome/jmods/java.base.jmod"
//        )

    // Add all JDK deps
//    if( ! properties("skipProguard").toBoolean()) {
        File("$javaHome/jmods/").listFiles()!!.forEach { libraryjars(it.absolutePath) }
//    }

//    libraryjars(configurations.runtimeClasspath.get().files)
//    val ideaPath = getIDEAPath()

    // Add all java plugins to classpath
//    File("$ideaPath/plugins/java/lib").listFiles()!!.forEach { libraryjars(it.absolutePath) }
    // Add all IDEA libs to classpath
//    File("$ideaPath/lib").listFiles()!!.forEach { libraryjars(it.absolutePath) }

    libraryjars(configurations.compileClasspath.get())

    dontshrink()
    dontoptimize()
    //useuniqueclassmembernames()

//    allowaccessmodification() //you probably shouldn't use this option when processing code that is to be used as a library, since classes and class members that weren't designed to be public in the API may become public

    adaptclassstrings("**.xml")
    adaptresourcefilecontents("**.xml")// or   adaptresourcefilecontents()

    // Allow methods with the same signature, except for the return type,
    // to get the same obfuscation name.
    //overloadaggressively()
    // Put all obfuscated classes into the nameless root package.
//    repackageclasses("")

    printmapping("build/proguard-mapping.txt")

    adaptresourcefilenames()
    optimizationpasses(9)
    allowaccessmodification()
//    mergeinterfacesaggressively()
    renamesourcefileattribute("SourceDir")
    keepattributes("Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod")

    obfuscationdictionary("dic.txt")

    // 保留除指定包路径外的所有类不被混淆
    keep("""
        class !com.xiaomi.youpin.tesla.ip.common.**,
              !com.xiaomi.youpin.tesla.ip.service.**,
              !run.mone.ultraman.common.**,
              !run.mone.ultraman.service.** {
                    *;
                }
    """.trimIndent())

    // 允许特定包路径下的类被混淆
//    keep("""
//        class com.xiaomi.youpin.tesla.ip.common.** { *; }
//        class com.xiaomi.youpin.tesla.ip.service.** { *; }
//        class run.mone.ultraman.common.** { *; }
//    """.trimIndent())

}


//tasks {
//    prepareSandbox {
//            dependsOn("proguard")
//            doFirst {
//                println("${rootProject.name}-${rootProject.version}")
//                val original = File("build/libs/instrumented-${rootProject.name}-${rootProject.version}.jar")
//                println(original.absolutePath)
//                val obfuscated =  File("build/instrumented-${rootProject.name}-obfuscated.jar")
//                println(obfuscated.absolutePath)
//                if (original.exists() && obfuscated.exists()) {
//                    original.delete()
//                    obfuscated.renameTo(original)
//                    println("info: plugin file obfuscated")
//                } else {
//                    println("error: some file does not exist, plugin file not obfuscated")
//                }
//            }
//    }
//}