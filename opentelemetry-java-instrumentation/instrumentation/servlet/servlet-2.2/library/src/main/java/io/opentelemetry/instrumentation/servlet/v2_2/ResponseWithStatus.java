/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.servlet.v2_2;

import javax.servlet.http.HttpServletResponse;

public class ResponseWithStatus {

  private final HttpServletResponse response;
  private final int status;

  public ResponseWithStatus(HttpServletResponse response, int status) {
    this.response = response;
    this.status = status;
  }

  public HttpServletResponse getResponse() {
    return response;
  }

  public int getStatus() {
    return status;
  }
}
