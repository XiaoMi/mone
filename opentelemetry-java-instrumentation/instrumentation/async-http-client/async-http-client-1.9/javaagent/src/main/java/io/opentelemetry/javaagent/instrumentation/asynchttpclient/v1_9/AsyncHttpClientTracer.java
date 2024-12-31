/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.asynchttpclient.v1_9;

import com.ning.http.client.Request;
import com.ning.http.client.Response;
import io.opentelemetry.context.propagation.TextMapSetter;
import io.opentelemetry.instrumentation.api.tracer.HttpClientTracer;
import io.opentelemetry.instrumentation.api.tracer.net.NetPeerAttributes;
import java.net.URI;
import java.net.URISyntaxException;

public class AsyncHttpClientTracer extends HttpClientTracer<Request, Request, Response> {

  private static final AsyncHttpClientTracer TRACER = new AsyncHttpClientTracer();

  private AsyncHttpClientTracer() {
    super(NetPeerAttributes.INSTANCE);
  }

  public static AsyncHttpClientTracer tracer() {
    return TRACER;
  }

  @Override
  protected String method(Request request) {
    return request.getMethod();
  }

  @Override
  protected URI url(Request request) throws URISyntaxException {
    return request.getUri().toJavaNetURI();
  }

  @Override
  protected Integer status(Response response) {
    return response.getStatusCode();
  }

  @Override
  protected String requestHeader(Request request, String name) {
    return request.getHeaders().getFirstValue(name);
  }

  @Override
  protected String responseHeader(Response response, String name) {
    return response.getHeaders().getFirstValue(name);
  }

  @Override
  protected TextMapSetter<Request> getSetter() {
    return AsyncHttpClientInjectAdapter.SETTER;
  }

  @Override
  protected String getInstrumentationName() {
    return "io.opentelemetry.javaagent.async-http-client-1.9";
  }
}
