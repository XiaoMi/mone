/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.sdk.trace.export;

import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.testing.trace.TestSpanData;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.data.StatusData;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Benchmark)
public class MultiSpanExporterBenchmark {

  private static class NoopSpanExporter implements SpanExporter {

    @Override
    public CompletableResultCode export(Collection<SpanData> spans) {
      return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode flush() {
      return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode shutdown() {
      return CompletableResultCode.ofSuccess();
    }
  }

  @Param({"1", "3"})
  private int exporterCount;

  private SpanExporter exporter;

  @Param({"1000"})
  private int spanCount;

  private List<SpanData> spans;

  @Setup(Level.Trial)
  public final void setup() {
    SpanExporter[] exporter = new SpanExporter[exporterCount];
    Arrays.fill(exporter, new NoopSpanExporter());
    this.exporter = SpanExporter.composite(Arrays.asList(exporter));

    TestSpanData[] spans = new TestSpanData[spanCount];
    for (int i = 0; i < spans.length; i++) {
      spans[i] =
          TestSpanData.builder()
              .setSpanContext(
                  SpanContext.create(
                      "12345678876543211234567887654321",
                      "8765432112345678",
                      TraceFlags.getSampled(),
                      TraceState.getDefault()))
              .setName("noop")
              .setKind(SpanKind.CLIENT)
              .setStartEpochNanos(1)
              .setStatus(StatusData.ok())
              .setEndEpochNanos(2)
              .setHasEnded(true)
              .build();
    }
    this.spans = Arrays.asList(spans);
  }

  @Benchmark
  @Fork(1)
  @Warmup(iterations = 5, time = 1)
  @Measurement(iterations = 10, time = 1)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  public CompletableResultCode export() {
    return exporter.export(spans);
  }
}
