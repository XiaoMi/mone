/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.api.instrumenter;

import java.time.Instant;

/**
 * Extractor of the end time of response processing. An {@link EndTimeExtractor} should always use
 * the same timestamp source as the corresponding {@link StartTimeExtractor} - extracted timestamps
 * must be comparable.
 */
@FunctionalInterface
public interface EndTimeExtractor<RESPONSE> {

  /** Returns the timestamp marking the end of the response processing. */
  Instant extract(RESPONSE response);
}
