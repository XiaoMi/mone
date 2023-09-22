/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.sdk.metrics;

import com.google.auto.value.AutoValue;
import io.opentelemetry.sdk.common.InstrumentationLibraryInfo;
import javax.annotation.concurrent.Immutable;

@AutoValue
@Immutable
abstract class MeterSharedState {
  static MeterSharedState create(InstrumentationLibraryInfo instrumentationLibraryInfo) {
    return new AutoValue_MeterSharedState(instrumentationLibraryInfo, new InstrumentRegistry());
  }

  abstract InstrumentationLibraryInfo getInstrumentationLibraryInfo();

  abstract InstrumentRegistry getInstrumentRegistry();
}
