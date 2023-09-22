/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.smoketest

import static java.util.stream.Collectors.toSet

import io.opentelemetry.api.trace.TraceId
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest
import spock.lang.IgnoreIf

abstract class PropagationTest extends SmokeTest {

  @Override
  protected String getTargetImage(String jdk) {
    "ghcr.io/open-telemetry/java-test-containers:smoke-springboot-jdk$jdk-20210218.577304949"
  }

  def "Should propagate test"() {
    setup:
    startTarget(11)
    when:
    def response = client().get("/front").aggregate().join()
    Collection<ExportTraceServiceRequest> traces = waitForTraces()
    def traceIds = getSpanStream(traces)
      .map({ TraceId.fromBytes(it.getTraceId().toByteArray()) })
      .collect(toSet())

    then:
    traceIds.size() == 1

    def traceId = traceIds.first()

    response.contentUtf8() == "${traceId};${traceId}"

    cleanup:
    stopTarget()

  }

}

@IgnoreIf({ os.windows })
class DefaultPropagationTest extends PropagationTest {
}

@IgnoreIf({ os.windows })
class W3CPropagationTest extends PropagationTest {
  @Override
  protected Map<String, String> getExtraEnv() {
    return ["otel.propagators": "tracecontext"]
  }
}

@IgnoreIf({ os.windows })
class B3PropagationTest extends PropagationTest {
  @Override
  protected Map<String, String> getExtraEnv() {
    return ["otel.propagators": "b3"]
  }
}

@IgnoreIf({ os.windows })
class B3MultiPropagationTest extends PropagationTest {
  @Override
  protected Map<String, String> getExtraEnv() {
    return ["otel.propagators": "b3multi"]
  }
}

@IgnoreIf({ os.windows })
class JaegerPropagationTest extends PropagationTest {
  @Override
  protected Map<String, String> getExtraEnv() {
    return ["otel.propagators": "jaeger"]
  }
}

@IgnoreIf({ os.windows })
class OtTracePropagationTest extends SmokeTest {
  @Override
  protected String getTargetImage(String jdk) {
    "ghcr.io/open-telemetry/java-test-containers:smoke-springboot-jdk$jdk-20210218.577304949"
  }

  // OtTracer only propagates lower half of trace ID so we have to mangle the trace IDs similar to
  // the Lightstep backend.
  def "Should propagate test"() {
    setup:
    startTarget(11)
    when:
    def response = client().get("/front").aggregate().join()
    Collection<ExportTraceServiceRequest> traces = waitForTraces()
    def traceIds = getSpanStream(traces)
      .map({ TraceId.fromBytes(it.getTraceId().toByteArray()).substring(16) })
      .collect(toSet())

    then:
    traceIds.size() == 1

    def traceId = traceIds.first()

    response.contentUtf8().matches(/[0-9a-f]{16}${traceId};[0]{16}${traceId}/)

    cleanup:
    stopTarget()
  }

  @Override
  protected Map<String, String> getExtraEnv() {
    return ["otel.propagators": "ottrace"]
  }
}

@IgnoreIf({ os.windows })
class XRayPropagationTest extends PropagationTest {
  @Override
  protected Map<String, String> getExtraEnv() {
    return ["otel.propagators": "xray"]
  }
}
