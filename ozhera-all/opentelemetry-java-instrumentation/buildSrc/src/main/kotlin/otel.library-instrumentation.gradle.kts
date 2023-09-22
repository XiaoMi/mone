plugins {
  id("otel.java-conventions")
  id("otel.jacoco-conventions")
  id("otel.publish-conventions")
  id("otel.instrumentation-conventions")
}

extra["mavenGroupId"] = "io.opentelemetry.instrumentation"

base.archivesBaseName = projectDir.parentFile.name

dependencies {
  api(project(":instrumentation-api"))

  api("run.mone:opentelemetry-api")

  testImplementation(project(":testing-common"))
}
