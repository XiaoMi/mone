/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.grpc.v1_6;

import io.grpc.Metadata;
import io.opentelemetry.context.propagation.TextMapGetter;
import org.checkerframework.checker.nullness.qual.Nullable;

final class GrpcExtractAdapter implements TextMapGetter<GrpcRequest> {

  static final GrpcExtractAdapter GETTER = new GrpcExtractAdapter();

  @Override
  public Iterable<String> keys(GrpcRequest request) {
    return request.getMetadata().keys();
  }

  @Override
  @Nullable
  public String get(@Nullable GrpcRequest request, String key) {
    if (request == null) {
      return null;
    }
    return request.getMetadata().get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER));
  }
}
