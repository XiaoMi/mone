import io.opentelemetry.instrumentation.gradle.OtelJavaExtension
import java.time.Duration
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
  `java-library`
  groovy
  checkstyle
  codenarc

  id("org.gradle.test-retry")
  id("net.ltgt.errorprone")

  id("otel.spotless-conventions")
}

val otelJava = extensions.create<OtelJavaExtension>("otelJava")

afterEvaluate {
  if (findProperty("mavenGroupId") == "io.opentelemetry.javaagent.instrumentation") {
    base.archivesBaseName = "opentelemetry-javaagent-${base.archivesBaseName}"
  } else {
    base.archivesBaseName = "opentelemetry-${base.archivesBaseName}"
  }
}

// Version to use to compile code and run tests.
val DEFAULT_JAVA_VERSION = JavaVersion.VERSION_11

java {
  toolchain {
    languageVersion.set(otelJava.minJavaVersionSupported.map { JavaLanguageVersion.of(Math.max(it.majorVersion.toInt(), DEFAULT_JAVA_VERSION.majorVersion.toInt())) })
  }

  // See https://docs.gradle.org/current/userguide/upgrading_version_5.html, Automatic target JVM version
  disableAutoTargetJvm()
  withJavadocJar()
  withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
  with(options) {
    release.set(otelJava.minJavaVersionSupported.map { it.majorVersion.toInt() })
    compilerArgs.add("-Werror")
  }
}

// Groovy and Scala compilers don't actually understand --release option
afterEvaluate {
  tasks.withType<GroovyCompile>().configureEach {
    sourceCompatibility = otelJava.minJavaVersionSupported.get().majorVersion
    targetCompatibility = otelJava.minJavaVersionSupported.get().majorVersion
  }
  tasks.withType<ScalaCompile>().configureEach {
    sourceCompatibility = otelJava.minJavaVersionSupported.get().majorVersion
    targetCompatibility = otelJava.minJavaVersionSupported.get().majorVersion
  }
}

evaluationDependsOn(":dependencyManagement")
val dependencyManagementConf = configurations.create("dependencyManagement") {
  isCanBeConsumed = false
  isCanBeResolved = false
  isVisible = false
}
afterEvaluate {
  configurations.configureEach {
    if (isCanBeResolved && !isCanBeConsumed) {
      extendsFrom(dependencyManagementConf)
    }
  }
}

// Force 4.0, or 4.1 to the highest version of that branch. Since 4.0 and 4.1 often have
// compatibility issues we can't just force to the highest version using normal BOM dependencies.
abstract class NettyAlignmentRule : ComponentMetadataRule {
  override fun execute(ctx: ComponentMetadataContext) {
    with(ctx.details) {
      if (id.group == "io.netty" && id.name != "netty") {
        if (id.version.startsWith("4.1.")) {
          belongsTo("io.netty:netty-bom:4.1.65.Final", false)
        } else if (id.version.startsWith("4.0.")) {
          belongsTo("io.netty:netty-bom:4.0.56.Final", false)
        }
      }
    }
  }
}

dependencies {
  add(dependencyManagementConf.name, platform(project(":dependencyManagement")))

  components.all<NettyAlignmentRule>()

  compileOnly("org.checkerframework:checker-qual")

  testImplementation("org.junit.jupiter:junit-jupiter-api")
  testImplementation("org.junit.jupiter:junit-jupiter-params")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
  testRuntimeOnly("org.junit.vintage:junit-vintage-engine")

  testImplementation("org.objenesis:objenesis")
  testImplementation("org.spockframework:spock-core")
  testImplementation("ch.qos.logback:logback-classic")
  testImplementation("org.slf4j:log4j-over-slf4j")
  testImplementation("org.slf4j:jcl-over-slf4j")
  testImplementation("org.slf4j:jul-to-slf4j")
  testImplementation("info.solidsoft.spock:spock-global-unroll")
  testImplementation("com.github.stefanbirkner:system-rules")
}

tasks {
  named<Jar>("jar") {
    // By default Gradle Jar task can put multiple files with the same name
    // into a Jar. This may lead to confusion. For example if auto-service
    // annotation processing creates files with same name in `scala` and
    // `java` directory this would result in Jar having two files with the
    // same name in it. Which in turn would result in only one of those
    // files being actually considered when that Jar is used leading to very
    // confusing failures. Instead we should 'fail early' and avoid building such Jars.
    duplicatesStrategy = DuplicatesStrategy.FAIL

    manifest {
      attributes(
        "Implementation-Title" to project.name,
        "Implementation-Version" to project.version,
        "Implementation-Vendor" to "OpenTelemetry",
        "Implementation-URL" to "https://github.com/open-telemetry/opentelemetry-java-instrumentation"
      )
    }
  }

  named<Javadoc>("javadoc") {
    with(options as StandardJavadocDocletOptions) {
      source = "8"
      encoding = "UTF-8"
      docEncoding = "UTF-8"
      charSet = "UTF-8"
      breakIterator(true)

      links("https://docs.oracle.com/javase/8/docs/api/")

      addStringOption("Xdoclint:none", "-quiet")
      // non-standard option to fail on warnings, see https://bugs.openjdk.java.net/browse/JDK-8200363
      addStringOption("Xwerror", "-quiet")
    }
  }

  withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
  }
}

normalization {
  runtimeClasspath {
    metaInf {
      ignoreAttribute("Implementation-Version")
    }
  }
}

fun isJavaVersionAllowed(version: JavaVersion): Boolean {
  if (otelJava.minJavaVersionSupported.get().compareTo(version) > 0) {
    return false
  }
  if (otelJava.maxJavaVersionForTests.isPresent() && otelJava.maxJavaVersionForTests.get().compareTo(version) < 0) {
    return false
  }
  return true
}

val testJavaVersion = gradle.startParameter.projectProperties.get("testJavaVersion")?.let(JavaVersion::toVersion)
val resourceClassesCsv = listOf("Host", "Os", "Process", "ProcessRuntime").map { "io.opentelemetry.sdk.extension.resources.${it}ResourceProvider" }.joinToString(",")
tasks.withType<Test>().configureEach {
  useJUnitPlatform()

  // There's no real harm in setting this for all tests even if any happen to not be using context
  // propagation.
  jvmArgs("-Dio.opentelemetry.context.enableStrictContext=${rootProject.findProperty("enableStrictContext") ?: false}")
  // TODO(anuraaga): Have agent map unshaded to shaded.
  jvmArgs("-Dio.opentelemetry.javaagent.shaded.io.opentelemetry.context.enableStrictContext=${rootProject.findProperty("enableStrictContext") ?: false}")

  // Disable default resource providers since they cause lots of output we don't need.
  jvmArgs("-Dotel.java.disabled.resource.providers=${resourceClassesCsv}")

  val trustStore = project(":testing-common").file("src/misc/testing-keystore.p12")
  inputs.file(trustStore)
  // Work around payara not working when this is set for some reason.
  if (project.name != "jaxrs-2.0-payara-testing") {
    jvmArgs("-Djavax.net.ssl.trustStore=${trustStore.absolutePath}")
    jvmArgs("-Djavax.net.ssl.trustStorePassword=testing")
  }

  // All tests must complete within 15 minutes.
  // This value is quite big because with lower values (3 mins) we were experiencing large number of false positives
  timeout.set(Duration.ofMinutes(15))

  retry {
    // You can see tests that were retried by this mechanism in the collected test reports and build scans.
    maxRetries.set(if (System.getenv("CI") != null) 5 else 0)
  }

  reports {
    junitXml.isOutputPerTestCase = true
  }

  testLogging {
    exceptionFormat = TestExceptionFormat.FULL
  }
}

afterEvaluate {
  tasks.withType<Test>().configureEach {
    if (testJavaVersion != null) {
      javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(testJavaVersion.majorVersion))
      })
      isEnabled = isJavaVersionAllowed(testJavaVersion)
    } else {
      // We default to testing with Java 11 for most tests, but some tests don't support it, where we change
      // the default test task's version so commands like `./gradlew check` can test all projects regardless
      // of Java version.
      if (!isJavaVersionAllowed(DEFAULT_JAVA_VERSION) && otelJava.maxJavaVersionForTests.isPresent) {
        javaLauncher.set(javaToolchains.launcherFor {
          languageVersion.set(JavaLanguageVersion.of(otelJava.maxJavaVersionForTests.get().majorVersion))
        })
      }
    }

    if (plugins.hasPlugin("org.unbroken-dome.test-sets") && configurations.findByName("latestDepTestRuntime") != null) {
      doFirst {
        val testArtifacts = configurations.testRuntimeClasspath.get().resolvedConfiguration.resolvedArtifacts
        val latestTestArtifacts = configurations.getByName("latestDepTestRuntimeClasspath").resolvedConfiguration.resolvedArtifacts
        if (testArtifacts == latestTestArtifacts) {
          throw IllegalStateException("latestDepTest dependencies are identical to test")
        }
      }
    }
  }
}

codenarc {
  configFile = rootProject.file("gradle/enforcement/codenarc.groovy")
  toolVersion = "2.0.0"
}

checkstyle {
  configFile = rootProject.file("gradle/enforcement/checkstyle.xml")
  // this version should match the version of google_checks.xml used as basis for above configuration
  toolVersion = "8.37"
  maxWarnings = 0
}
