/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.liberty.dispatcher;

import com.ibm.ws.http.dispatcher.internal.channel.HttpDispatcherLink;
import com.ibm.wsspi.genericbnf.HeaderField;
import com.ibm.wsspi.http.channel.HttpRequestMessage;
import java.util.List;

public class LibertyRequestWrapper {
  private final HttpDispatcherLink httpDispatcherLink;
  private final HttpRequestMessage httpRequestMessage;

  public LibertyRequestWrapper(
      HttpDispatcherLink httpDispatcherLink, HttpRequestMessage httpRequestMessage) {
    this.httpDispatcherLink = httpDispatcherLink;
    this.httpRequestMessage = httpRequestMessage;
  }

  public String getMethod() {
    return httpRequestMessage.getMethod();
  }

  public String getScheme() {
    return httpRequestMessage.getScheme();
  }

  public String getRequestUri() {
    return httpRequestMessage.getRequestURI();
  }

  public String getQueryString() {
    return httpRequestMessage.getQueryString();
  }

  public String getServerName() {
    return httpDispatcherLink.getRequestedHost();
  }

  public int getServerPort() {
    return httpDispatcherLink.getRequestedPort();
  }

  public List<String> getAllHeaderNames() {
    return httpRequestMessage.getAllHeaderNames();
  }

  public String getHeaderValue(String name) {
    HeaderField hf = httpRequestMessage.getHeader(name);
    return hf != null ? hf.asString() : null;
  }
}
