package run.mone.m78.server.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import run.mone.m78.api.bo.contact.ContactParam;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.service.contact.ContactService;
import run.mone.m78.service.service.user.LoginService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;


@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/contact")
@HttpApiModule(value = "ContactController", apiController = ContactController.class)
public class ContactController {

    @Autowired
    private LoginService loginService;

    @Resource
    private ContactService contactService;


    @PostMapping(value = "/submit")
    public Result<Integer> submit(HttpServletRequest request, @RequestBody ContactParam contactParam) {
        SessionAccount account = loginService.getAccountFromSession(request);
        contactParam.setUserName(account.getUsername());
        return contactService.submit(contactParam);
    }

}
