/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.awslambda.v1_0;

import static io.opentelemetry.instrumentation.awslambda.v1_0.MapUtils.emptyIfNull;
import static io.opentelemetry.instrumentation.awslambda.v1_0.MapUtils.lowercaseMap;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.HTTP_METHOD;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.HTTP_STATUS_CODE;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.HTTP_URL;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.HTTP_USER_AGENT;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

final class HttpSpanAttributes {
  static void onRequest(SpanBuilder span, APIGatewayProxyRequestEvent request) {
    String httpMethod = request.getHttpMethod();
    if (httpMethod != null) {
      span.setAttribute(HTTP_METHOD, httpMethod);
    }

    Map<String, String> headers = lowercaseMap(request.getHeaders());
    String userAgent = headers.get("user-agent");
    if (userAgent != null) {
      span.setAttribute(HTTP_USER_AGENT, userAgent);
    }
    String url = getHttpUrl(request, headers);
    if (!url.isEmpty()) {
      span.setAttribute(HTTP_URL, url);
    }
  }

  private static String getHttpUrl(
      APIGatewayProxyRequestEvent request, Map<String, String> headers) {
    StringBuilder str = new StringBuilder();

    String scheme = headers.get("x-forwarded-proto");
    if (scheme != null) {
      str.append(scheme).append("://");
    }
    String host = headers.get("host");
    if (host != null) {
      str.append(host);
    }
    String path = request.getPath();
    if (path != null) {
      str.append(path);
    }

    try {
      boolean first = true;
      for (Map.Entry<String, String> entry :
          emptyIfNull(request.getQueryStringParameters()).entrySet()) {
        String key = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.name());
        String value = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name());
        str.append(first ? '?' : '&').append(key).append('=').append(value);
        first = false;
      }
    } catch (UnsupportedEncodingException ignored) {
      // Ignore
    }
    return str.toString();
  }

  static void onResponse(Span span, APIGatewayProxyResponseEvent response) {
    Integer statusCode = response.getStatusCode();
    if (statusCode != null) {
      span.setAttribute(HTTP_STATUS_CODE, statusCode);
    }
  }

  private HttpSpanAttributes() {}
}
