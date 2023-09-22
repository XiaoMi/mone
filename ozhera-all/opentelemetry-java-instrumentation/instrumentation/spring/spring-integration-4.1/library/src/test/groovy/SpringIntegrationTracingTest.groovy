/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

import io.opentelemetry.instrumentation.test.LibraryTestTrait

class SpringIntegrationTracingTest extends AbstractSpringIntegrationTracingTest implements LibraryTestTrait {
  @Override
  Class<?> additionalContextClass() {
    GlobalInterceptorSpringConfig
  }
}
