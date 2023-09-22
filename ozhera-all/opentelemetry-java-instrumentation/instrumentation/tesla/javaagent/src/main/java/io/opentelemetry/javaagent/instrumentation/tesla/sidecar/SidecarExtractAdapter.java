/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.tesla.sidecar;

import com.xiaomi.data.push.uds.po.RpcCommand;
import io.opentelemetry.context.propagation.TextMapGetter;

public class SidecarExtractAdapter implements TextMapGetter<RpcCommand> {

  public static final SidecarExtractAdapter GETTER = new SidecarExtractAdapter();

  @Override
  public Iterable<String> keys(RpcCommand rpcCommand) {
    return rpcCommand.getAttachments().keySet();
  }

  @Override
  public String get(RpcCommand carrier, String key) {
    return carrier.getAttachments().get(key);
  }
}
