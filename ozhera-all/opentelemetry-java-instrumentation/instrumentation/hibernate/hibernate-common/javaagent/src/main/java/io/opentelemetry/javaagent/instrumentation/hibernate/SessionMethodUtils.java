/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.hibernate;

import static io.opentelemetry.javaagent.instrumentation.hibernate.HibernateTracer.tracer;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.db.SqlStatementInfo;
import io.opentelemetry.instrumentation.api.db.SqlStatementSanitizer;
import io.opentelemetry.javaagent.instrumentation.api.CallDepthThreadLocalMap;
import io.opentelemetry.javaagent.instrumentation.api.ContextStore;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class SessionMethodUtils {

  public static final Set<String> SCOPE_ONLY_METHODS =
      new HashSet<>(Arrays.asList("immediateLoad", "internalLoad"));

  public static <TARGET, ENTITY> Context startSpanFrom(
      ContextStore<TARGET, Context> contextStore,
      TARGET spanKey,
      String operationName,
      ENTITY entity) {
    return startSpanFrom(contextStore, spanKey, () -> operationName, entity);
  }

  private static <TARGET, ENTITY> Context startSpanFrom(
      ContextStore<TARGET, Context> contextStore,
      TARGET spanKey,
      Supplier<String> operationNameSupplier,
      ENTITY entity) {

    Context sessionContext = contextStore.get(spanKey);
    if (sessionContext == null) {
      return null; // No state found. We aren't in a Session.
    }

    int depth = CallDepthThreadLocalMap.incrementCallDepth(SessionMethodUtils.class);
    if (depth > 0) {
      return null; // This method call is being traced already.
    }

    return tracer().startSpan(sessionContext, operationNameSupplier.get(), entity);
  }

  public static <TARGET> Context startSpanFromQuery(
      ContextStore<TARGET, Context> contextStore, TARGET spanKey, String query) {
    Supplier<String> operationNameSupplier =
        () -> {
          // set operation to default value that is used when sql sanitizer fails to extract
          // operation name
          String operation = "Hibernate Query";
          SqlStatementInfo info = SqlStatementSanitizer.sanitize(query);
          if (info.getOperation() != null) {
            operation = info.getOperation();
            if (info.getTable() != null) {
              operation += " " + info.getTable();
            }
          }
          return operation;
        };
    return startSpanFrom(contextStore, spanKey, operationNameSupplier, null);
  }

  public static void end(
      @Nullable Context context, Throwable throwable, String operationName, Object entity) {

    CallDepthThreadLocalMap.reset(SessionMethodUtils.class);

    if (context == null) {
      return;
    }

    if (operationName != null && entity != null) {
      String entityName = tracer().entityName(entity);
      if (entityName != null) {
        Span.fromContext(context).updateName(operationName + " " + entityName);
      }
    }
    if (throwable != null) {
      tracer().endExceptionally(context, throwable);
    } else {
      tracer().end(context);
    }
  }

  // Copies a span from the given Session ContextStore into the targetContextStore. Used to
  // propagate a Span from a Session to transient Session objects such as Transaction and Query.
  public static <S, T> void attachSpanFromStore(
      ContextStore<S, Context> sourceContextStore,
      S source,
      ContextStore<T, Context> targetContextStore,
      T target) {

    Context sessionContext = sourceContextStore.get(source);
    if (sessionContext == null) {
      return;
    }

    targetContextStore.putIfAbsent(target, sessionContext);
  }

  private SessionMethodUtils() {}
}
