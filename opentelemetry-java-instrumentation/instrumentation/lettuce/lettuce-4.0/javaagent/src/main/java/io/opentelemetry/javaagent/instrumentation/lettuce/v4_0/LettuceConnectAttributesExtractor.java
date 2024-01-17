/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.lettuce.v4_0;

import com.lambdaworks.redis.RedisURI;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;

final class LettuceConnectAttributesExtractor extends AttributesExtractor<RedisURI, Void> {

  @Override
  protected void onStart(AttributesBuilder attributes, RedisURI redisUri) {
    attributes.put(SemanticAttributes.DB_SYSTEM, SemanticAttributes.DbSystemValues.REDIS);

    int database = redisUri.getDatabase();
    if (database != 0) {
      attributes.put(SemanticAttributes.DB_REDIS_DATABASE_INDEX, (long) database);
    }
  }

  @Override
  protected void onEnd(AttributesBuilder attributes, RedisURI redisUri, Void unused) {}
}
