/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.jedis.v4_0.connect;

import io.opentelemetry.instrumentation.api.instrumenter.db.DbAttributesExtractor;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class JedisConnectDbAttributesExtractor extends DbAttributesExtractor<JedisConnectionRequest, Void> {
  @Override
  protected String system(JedisConnectionRequest request) {
    return SemanticAttributes.DbSystemValues.REDIS;
  }

  @Override
  @Nullable
  protected String user(JedisConnectionRequest request) {
    return null;
  }

  @Override
  protected String name(JedisConnectionRequest request) {
    return null;
  }

  @Override
  protected String connectionString(JedisConnectionRequest request) {
    return null;
  }

  @Override
  protected String statement(JedisConnectionRequest request) {
    return "connect";
  }

  @Override
  protected String operation(JedisConnectionRequest request) {
    return "connect";
  }
}
