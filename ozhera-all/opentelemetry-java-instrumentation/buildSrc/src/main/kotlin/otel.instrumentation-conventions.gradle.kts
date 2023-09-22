/** Common setup for manual instrumentation of libraries and javaagent instrumentation. */

/**
 * We define three dependency configurations to use when adding dependencies to libraries being
 * instrumented.
 *
 * - library: A dependency on the instrumented library. Results in the dependency being added to
 *     compileOnly and testImplementation. If the build is run with -PtestLatestDeps=true, the
 *     version when added to testImplementation will be overridden by `+`, the latest version
 *     possible. For simple libraries without different behavior between versions, it is possible
 *     to have a single dependency on library only.
 *
 * - testLibrary: A dependency on a library for testing. This will usually be used to either
 *     a) use a different version of the library for compilation and testing and b) to add a helper
 *     that is only required for tests (e.g., library-testing artifact). The dependency will be
 *     added to testImplementation and will have a version of `+` when testing latest deps as
 *     described above.
 *
 * - latestDepTestLibrary: A dependency on a library for testing when testing of latest dependency
 *   version is enabled. This dependency will be added as-is to testImplementation, but only if
 *   -PtestLatestDeps=true. The version will not be modified but it will be given highest
 *   precedence. Use this to restrict the latest version dependency from the default `+`, for
 *   example to restrict to just a major version by specifying `2.+`.
 */

val testLatestDeps = gradle.startParameter.projectProperties["testLatestDeps"] == "true"
extra["testLatestDeps"] = testLatestDeps

configurations {
  val library by creating {
    isCanBeResolved = false
    isCanBeConsumed = false
  }
  val testLibrary by creating {
    isCanBeResolved = false
    isCanBeConsumed = false
  }
  val latestDepTestLibrary by creating {
    isCanBeResolved = false
    isCanBeConsumed = false
  }

  val testImplementation by getting

  listOf(library, testLibrary).forEach { configuration ->
    // We use whenObjectAdded and copy into the real configurations instead of extension to allow
    // mutating the version for latest dep tests.
    configuration.dependencies.whenObjectAdded {
      val dep = copy()
      if (testLatestDeps) {
        (dep as ExternalDependency).version {
          require("+")
        }
      }
      testImplementation.dependencies.add(dep)
    }
  }
  if (testLatestDeps) {
    latestDepTestLibrary.dependencies.whenObjectAdded {
      val dep = copy()
      val declaredVersion = dep.version
      if (declaredVersion != null) {
        (dep as ExternalDependency).version {
          strictly(declaredVersion)
        }
      }
      testImplementation.dependencies.add(dep)
    }
  }
  named("compileOnly") {
    extendsFrom(library)
  }
}

if (testLatestDeps) {
  afterEvaluate {
    if (tasks.names.contains("latestDepTest")) {
      val latestDepTest by tasks.existing
      tasks.named("test").configure {
        dependsOn(latestDepTest)
      }
    }
  }
}

when (projectDir.name) {
  "javaagent", "library", "testing" -> {
    // We don't use this group anywhere in our config, but we need to make sure it is unique per
    // instrumentation so Gradle doesn't merge projects with same name due to a bug in Gradle.
    // https://github.com/gradle/gradle/issues/847
    // In otel.publish-conventions, we set the maven group, which is what matters, to the correct
    // value.
    group = "io.opentelemetry.${projectDir.parentFile.name}"
  }
}
