/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.sdk.metrics.aggregator;

import io.opentelemetry.sdk.common.InstrumentationLibraryInfo;
import io.opentelemetry.sdk.metrics.common.InstrumentDescriptor;
import io.opentelemetry.sdk.resources.Resource;

final class LastValueAggregatorFactory implements AggregatorFactory {
  static final AggregatorFactory INSTANCE = new LastValueAggregatorFactory();

  private LastValueAggregatorFactory() {}

  @Override
  @SuppressWarnings("unchecked")
  public <T> Aggregator<T> create(
      Resource resource,
      InstrumentationLibraryInfo instrumentationLibraryInfo,
      InstrumentDescriptor descriptor) {
    switch (descriptor.getValueType()) {
      case LONG:
        return (Aggregator<T>)
            new LongLastValueAggregator(resource, instrumentationLibraryInfo, descriptor);
      case DOUBLE:
        return (Aggregator<T>)
            new DoubleLastValueAggregator(resource, instrumentationLibraryInfo, descriptor);
    }
    throw new IllegalArgumentException("Invalid instrument value type");
  }
}
