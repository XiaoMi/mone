/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.geode;

import com.google.auto.value.AutoValue;
import org.apache.geode.cache.Region;
import org.checkerframework.checker.nullness.qual.Nullable;

@AutoValue
public abstract class GeodeRequest {

  public static GeodeRequest create(Region<?, ?> region, String operation, @Nullable String query) {
    return new AutoValue_GeodeRequest(region, operation, query);
  }

  public abstract Region<?, ?> getRegion();

  public abstract String getOperation();

  @Nullable
  public abstract String getQuery();
}
