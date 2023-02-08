//package com.xiaomi.youpin.gwdash.filter;
//
//import com.xiaomi.youpin.gwdash.dao.AccountDao;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * @author tsingfu
// * 用户通过cas校验【config中的filter配置】后需要校验是否加入我们的account库
// */
//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE + 101)
//@Slf4j
//public class AuthFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private AccountDao accountDao;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
//                                    FilterChain filterChain) throws ServletException, IOException {
//
//        filterChain.doFilter(httpServletRequest, httpServletResponse);
////        HttpSession httpSession = httpServletRequest.getSession(false);
////        Assertion assertion = (Assertion) httpSession.getAttribute("_const_cas_assertion_");
////        String username = assertion.getPrincipal().getName();
////        Account account = accountDao.getByUsername(username);
////        if (null == account) {
////            httpServletRequest.getSession(false).invalidate();
////            httpServletResponse.sendError(403, "请联系业务架构组添加帐号");
////        }
//    }
//}
