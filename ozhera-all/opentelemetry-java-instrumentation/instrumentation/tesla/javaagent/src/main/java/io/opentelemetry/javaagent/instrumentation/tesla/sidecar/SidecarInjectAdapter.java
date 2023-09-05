/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.tesla.sidecar;

import com.xiaomi.data.push.uds.po.RpcCommand;
import io.opentelemetry.context.propagation.TextMapSetter;

public class SidecarInjectAdapter implements TextMapSetter<RpcCommand> {

  public static final SidecarInjectAdapter SETTER = new SidecarInjectAdapter();

  @Override
  public void set(RpcCommand rpcInvocation, String key, String value) {
    rpcInvocation.getAttachments().put(key, value);
  }
}
