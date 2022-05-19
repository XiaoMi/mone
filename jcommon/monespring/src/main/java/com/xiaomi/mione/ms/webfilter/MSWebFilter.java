package com.xiaomi.mione.ms.webfilter;

import org.apache.dubbo.rpc.RpcContext;

import javax.servlet.annotation.WebFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @Author goodjava@qq.com
 * @Date 2021/4/14 09:38
 * <p>
 * 保证traceid经过web controller 层也不间断
 */
@WebFilter(urlPatterns = "/*")
public class MSWebFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest req = (HttpServletRequest) request;
            String traceId = req.getHeader("X-Trace-Id");
            if (null != traceId && !traceId.equals("")) {
                RpcContext.getContext().setAttachment("_trace_id_", traceId);
            }
        }
        try {
            chain.doFilter(request, response);
        } finally {
            RpcContext.getContext().set("_trace_id_", null);
        }
    }

    @Override
    public void destroy() {

    }
}
