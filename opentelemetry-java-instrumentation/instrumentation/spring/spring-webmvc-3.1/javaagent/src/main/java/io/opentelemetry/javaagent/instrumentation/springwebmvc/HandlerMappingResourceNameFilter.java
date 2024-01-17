/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.springwebmvc;

import io.opentelemetry.context.Context;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class HandlerMappingResourceNameFilter implements Filter, Ordered {
  private volatile List<HandlerMapping> handlerMappings;

  @Override
  public void init(FilterConfig filterConfig) {}

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
      filterChain.doFilter(request, response);
      return;
    }

    Context context = Context.current();

    if (handlerMappings != null) {
      try {
        if (findMapping((HttpServletRequest) request)) {

          // Name the parent span based on the matching pattern
          // Let the parent span resource name be set with the attribute set in findMapping.
          ServerNameUpdater.updateServerSpanName(context, (HttpServletRequest) request);
        }
      } catch (Exception ignored) {
        // mapping.getHandler() threw exception.  Ignore
      }
    }

    filterChain.doFilter(request, response);
  }

  @Override
  public void destroy() {}

  /**
   * When a HandlerMapping matches a request, it sets HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE
   * as an attribute on the request. This attribute is read by SpringWebMvcDecorator.onRequest and
   * set as the resource name.
   */
  private boolean findMapping(HttpServletRequest request) throws Exception {
    for (HandlerMapping mapping : handlerMappings) {
      HandlerExecutionChain handler = mapping.getHandler(request);
      if (handler != null) {
        return true;
      }
    }
    return false;
  }

  public void setHandlerMappings(List<HandlerMapping> mappings) {
    List<HandlerMapping> handlerMappings = new ArrayList<>();
    for (HandlerMapping mapping : mappings) {
      // it may be enticing to add all HandlerMapping classes here, but DO NOT
      //
      // because we call getHandler() on them above, at the very beginning of the request
      // and this can be a very invasive call with application-crashing side-effects
      //
      // for example: org.grails.web.mapping.mvc.UrlMappingsHandlerMapping.getHandler()
      // 1. uses GrailsWebRequest.lookup() to get GrailsWebRequest bound to thread local
      // 2. and populates the servlet request attribute "org.grails.url.match.info"
      //    with GrailsControllerUrlMappingInfo
      //
      // which causes big problems if GrailsWebRequest thread local is leaked from prior request
      // (which has been observed to happen in Grails 3.0.17 at least), because then our call to
      // UrlMappingsHandlerMapping.getHandler() at the very beginning of the request:
      // 1. GrailsWebRequest.lookup() gets the leaked GrailsWebRequest
      // 2. servlet request attribute "org.grails.url.match.info" is populated based on this leaked
      //    GrailsWebRequest (so in other words, most likely the wrong route is matched)
      //
      // and then GrailsWebRequestFilter creates a new GrailsWebRequest and binds it to the thread
      //
      // and then the application calls UrlMappingsHandlerMapping.getHandler() to route the request
      // but it finds servlet request attribute "org.grails.url.match.info" already populated (by
      // above) and so it short cuts the matching process and uses the wrong route that the agent
      // populated caused to be populated into the request attribute above
      if (mapping instanceof RequestMappingHandlerMapping) {
        handlerMappings.add(mapping);
      }
    }
    if (!handlerMappings.isEmpty()) {
      this.handlerMappings = handlerMappings;
    }
  }

  @Override
  public int getOrder() {
    // Run after all HIGHEST_PRECEDENCE items
    return Ordered.HIGHEST_PRECEDENCE + 1;
  }

  public static class BeanDefinition extends GenericBeanDefinition {
    public BeanDefinition() {
      setScope(SCOPE_SINGLETON);
      setBeanClass(HandlerMappingResourceNameFilter.class);
      setBeanClassName(HandlerMappingResourceNameFilter.class.getName());
    }
  }
}
