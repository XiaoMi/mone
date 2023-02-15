package com.xiaomi.miapi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 * deal with logout request
 */
@Controller
public class LogoutController {

    @RequestMapping(value = "/logout", method = RequestMethod.POST, consumes = {"application/json"})
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "logout successfully";
    }
}
