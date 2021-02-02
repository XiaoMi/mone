/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.gwdash.controller;

import com.xiaomi.youpin.gwdash.common.Consts;
import com.xiaomi.youpin.gwdash.service.LoginService;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;


    @RequestMapping(value = "/api/validate", method = RequestMethod.GET)
    public void validate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        loginService.validate(request, response);
    }

    @RequestMapping(value = "/api/test", method = RequestMethod.GET)
    public String test(HttpServletRequest request) {
        Assertion assertion = (Assertion) request.getSession().getAttribute(Consts.CONST_CAS_ASSERTION);
        String userName = assertion.getPrincipal().getName();
        System.out.println(userName);

        return "login successfully";
    }

    @RequestMapping(value = "/api/logout", method = RequestMethod.POST, consumes = {"application/json"})
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
        return "logout successfully";
    }
}
