/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.grpc.v1_6

import io.grpc.ManagedChannelBuilder
import io.grpc.ServerBuilder
import io.opentelemetry.instrumentation.grpc.v1_6.AbstractGrpcTest
import io.opentelemetry.instrumentation.test.AgentTestTrait

class GrpcTest extends AbstractGrpcTest implements AgentTestTrait {
  @Override
  ServerBuilder configureServer(ServerBuilder server) {
    return server
  }

  @Override
  ManagedChannelBuilder configureClient(ManagedChannelBuilder client) {
    return client
  }
}
