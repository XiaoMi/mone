/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

import hello.HelloServiceImpl
import javax.servlet.ServletConfig
import org.apache.cxf.jaxws.EndpointImpl
import org.apache.cxf.transport.servlet.CXFNonSpringServlet

class TestWsServlet extends CXFNonSpringServlet {
  @Override
  void loadBus(ServletConfig servletConfig) {
    super.loadBus(servletConfig)

    // publish test webservice
    Object implementor = new HelloServiceImpl()
    EndpointImpl endpoint = new EndpointImpl(bus, implementor)
    endpoint.publish("/HelloService")
  }
}