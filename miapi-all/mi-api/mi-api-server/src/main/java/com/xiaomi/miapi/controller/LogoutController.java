package com.xiaomi.miapi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description TODO
 * @Author zhenxing.dong
 * @Date 2021/8/9 16:18
 */
@Controller
public class LogoutController {

    @RequestMapping(value = "/logout", method = RequestMethod.POST, consumes = {"application/json"})
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
        return "logout successfully";
    }
}
