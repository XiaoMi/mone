# OpenTelemetry Zipkin Exporter Starter

The OpenTelemetry  Exporter Starter for Java is a starter package that includes packages required to enable tracing using OpenTelemetry. It also provides the  dependency and corresponding auto-configuration.  Check out [opentelemetry-spring-boot-autoconfigure](../../spring-boot-autoconfigure/README.md#features) for the list of supported libraries and features.

OpenTelemetry Zipkin Exporter Starter is a starter package that includes the opentelemetry-api, opentelemetry-sdk, opentelemetry-extension-annotations, opentelmetry-logging-exporter, opentelemetry-spring-boot-autoconfigurations and spring framework starters required to setup distributed tracing. It also provides the [opentelemetry-exporters-zipkin](https://github.com/open-telemetry/opentelemetry-java/tree/master/exporters/zipkin) artifact and corresponding exporter auto-configuration.  Check out [opentelemetry-spring-boot-autoconfigure](../../spring-boot-autoconfigure/README.md#features) for the list of supported libraries and features.

## Quickstart

### Add these dependencies to your project.

Replace `OPENTELEMETRY_VERSION` with the latest stable [release](https://search.maven.org/search?q=g:io.opentelemetry).
 - Minimum version: `1.1.0`
 - Note: You may need to include our bintray maven repository to your build file: `https://dl.bintray.com/open-telemetry/maven/`. As of August 2020 the latest opentelemetry-java-instrumentation artifacts are not published to maven-central. Please check the [releasing](https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/master/RELEASING.md) doc for updates to this process.


#### Maven

```xml
<dependencies>

  <dependency>
    <groupId>io.opentelemetry.instrumentation</groupId>
    <artifactId>opentelemetry-zipkin-exporter-starter</artifactId>
    <version>OPENTELEMETRY_VERSION</version>
  </dependency>

</dependencies>
```

#### Gradle

```groovy
implementation 'io.opentelemetry.instrumentation:opentelemetry-zipkin-exporter-starter:OPENTELEMETRY_VERSION'
```

### Starter Guide

Check out the opentelemetry-api [quick start](https://github.com/open-telemetry/opentelemetry-java/blob/master/QUICKSTART.md) to learn more about OpenTelemetry instrumentation.
