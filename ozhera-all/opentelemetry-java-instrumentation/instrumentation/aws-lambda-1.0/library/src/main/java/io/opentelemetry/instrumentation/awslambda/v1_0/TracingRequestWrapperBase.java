/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.awslambda.v1_0;

import com.amazonaws.services.lambda.runtime.Context;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.autoconfigure.OpenTelemetrySdkAutoConfiguration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Base abstract wrapper for {@link TracingRequestHandler}. Provides: - delegation to a lambda via
 * env property OTEL_INSTRUMENTATION_AWS_LAMBDA_HANDLER in package.ClassName::methodName format
 */
abstract class TracingRequestWrapperBase<I, O> extends TracingRequestHandler<I, O> {

  private final WrappedLambda wrappedLambda;

  protected TracingRequestWrapperBase() {
    this(OpenTelemetrySdkAutoConfiguration.initialize(), WrappedLambda.fromConfiguration());
  }

  // Visible for testing
  TracingRequestWrapperBase(OpenTelemetrySdk openTelemetrySdk, WrappedLambda wrappedLambda) {
    super(openTelemetrySdk, WrapperConfiguration.flushTimeout());
    this.wrappedLambda = wrappedLambda;
  }

  private Object[] createParametersArray(Method targetMethod, I input, Context context) {
    Class<?>[] parameterTypes = targetMethod.getParameterTypes();
    Object[] parameters = new Object[parameterTypes.length];
    for (int i = 0; i < parameterTypes.length; i++) {
      // loop through to populate each index of parameter
      Object parameter = null;
      Class clazz = parameterTypes[i];
      boolean isContext = clazz.equals(Context.class);
      if (i == 0 && !isContext) {
        // first position if it's not context
        parameter = input;
      } else if (isContext) {
        // populate context
        parameter = context;
      }
      parameters[i] = parameter;
    }
    return parameters;
  }

  @Override
  protected O doHandleRequest(I input, Context context) {
    Method targetMethod = wrappedLambda.getRequestTargetMethod();
    Object[] parameters = createParametersArray(targetMethod, input, context);

    O result;
    try {
      result = (O) targetMethod.invoke(wrappedLambda.getTargetObject(), parameters);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("Method is inaccessible", e);
    } catch (InvocationTargetException e) {
      throw (e.getCause() instanceof RuntimeException
          ? (RuntimeException) e.getCause()
          : new IllegalStateException(e.getTargetException()));
    }
    return result;
  }
}
