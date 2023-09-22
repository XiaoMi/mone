/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.sdk.metrics.aggregator;

import io.opentelemetry.sdk.common.InstrumentationLibraryInfo;
import io.opentelemetry.sdk.metrics.common.InstrumentDescriptor;
import io.opentelemetry.sdk.metrics.data.AggregationTemporality;
import io.opentelemetry.sdk.resources.Resource;

final class CountAggregatorFactory implements AggregatorFactory {
  private final AggregationTemporality temporality;

  CountAggregatorFactory(AggregationTemporality temporality) {
    this.temporality = temporality;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> Aggregator<T> create(
      Resource resource,
      InstrumentationLibraryInfo instrumentationLibraryInfo,
      InstrumentDescriptor descriptor) {
    return (Aggregator<T>)
        new CountAggregator(resource, instrumentationLibraryInfo, descriptor, temporality);
  }
}
