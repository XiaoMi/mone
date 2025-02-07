package run.mone.m78.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import run.mone.m78.api.bo.prompt.PromptExecuteParam;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.service.prompt.PromptService;
import run.mone.m78.service.service.user.LoginService;

import javax.servlet.http.HttpServletRequest;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;

/**
 * @author wmin
 * @date 2024/2/23
 */
@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/prompt")
public class PromptController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private PromptService promptService;

    @RequestMapping(value = {"/execute/stream"}, method = RequestMethod.POST, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> generateMethodStream(HttpServletRequest request,
                                                           @RequestBody PromptExecuteParam param) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Accel-Buffering", "no");
        httpHeaders.setCacheControl(CacheControl.noCache());

        SessionAccount account = loginService.getAccountFromSession(request);
        param.setUser(account.getUsername());
        SseEmitter emitter = promptService.promptExecuteStream(param);

        return ResponseEntity.ok().contentType(MediaType.TEXT_EVENT_STREAM).headers(httpHeaders).body(emitter);
    }

}
