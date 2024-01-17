/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.jedis.v4_0;

import io.opentelemetry.instrumentation.api.instrumenter.db.DbAttributesExtractor;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;

import javax.annotation.Nullable;

final class JedisDbAttributesGetter extends DbAttributesExtractor<JedisRequest, Void> {

  @Override
  public String system(JedisRequest request) {
    return SemanticAttributes.DbSystemValues.REDIS;
  }

  @Override
  @Nullable
  public String user(JedisRequest request) {
    return null;
  }

  @Override
  public String name(JedisRequest request) {
    return null;
  }

  @Override
  public String connectionString(JedisRequest request) {
    return null;
  }

  @Override
  public String statement(JedisRequest request) {
    return request.getStatement();
  }

  @Override
  public String operation(JedisRequest request) {
    return request.getOperation();
  }
}
