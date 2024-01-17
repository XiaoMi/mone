/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.awslambda.v1_0;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.autoconfigure.OpenTelemetrySdkAutoConfiguration;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Wrapper for {@link TracingRequestStreamHandler}. Allows for wrapping a regular lambda, enabling
 * single span tracing. Main lambda class should be configured as env property
 * OTEL_INSTRUMENTATION_AWS_LAMBDA_HANDLER in package.ClassName::methodName format. Lambda class
 * must implement {@link RequestStreamHandler}.
 */
public class TracingRequestStreamWrapper extends TracingRequestStreamHandler {

  private final WrappedLambda wrappedLambda;

  public TracingRequestStreamWrapper() {
    this(OpenTelemetrySdkAutoConfiguration.initialize(), WrappedLambda.fromConfiguration());
  }

  // Visible for testing
  TracingRequestStreamWrapper(OpenTelemetrySdk openTelemetrySdk, WrappedLambda wrappedLambda) {
    super(openTelemetrySdk, WrapperConfiguration.flushTimeout());
    this.wrappedLambda = wrappedLambda;
  }

  @Override
  protected void doHandleRequest(InputStream inputStream, OutputStream output, Context context)
      throws IOException {

    if (!(wrappedLambda.getTargetObject() instanceof RequestStreamHandler)) {
      throw new IllegalStateException(
          wrappedLambda.getTargetClass().getName() + " is not an instance of RequestStreamHandler");
    }

    ((RequestStreamHandler) wrappedLambda.getTargetObject())
        .handleRequest(inputStream, output, context);
  }
}
