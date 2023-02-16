package com.xiaomi.hera.trace.etl.manager.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/6/6 10:15 上午
 */
public class RequestHeaderFilter implements Filter {

    private static Logger log = LoggerFactory.getLogger(RequestHeaderFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        printHeaders(request);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    private void printHeaders(HttpServletRequest request) {
        String uri = request.getRequestURI();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames == null || !headerNames.hasMoreElements()) {
            log.info(uri + " : rquest header is null");
        } else {
            log.info(uri + " : request header start ===================");
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                log.info(headerName + " : " + request.getHeader(headerName));
            }
            log.info(uri + " : request header end ===================");
        }
    }
}
