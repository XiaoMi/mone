package run.mone.m78.server.controller;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import run.mone.m78.api.bo.code.editor.EditorPath;
import run.mone.m78.api.bo.code.editor.GitProjectReq;
import run.mone.m78.api.bo.code.editor.FileReq;
import run.mone.m78.api.bo.code.editor.ScriptReq;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.service.api.Context;
import run.mone.m78.service.service.code.editor.CodeEditorService;
import run.mone.m78.service.service.user.LoginService;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;

@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/codeEditor")
@HttpApiModule(value = "CodeEditorController", apiController = CodeEditorController.class)
public class CodeEditorController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private CodeEditorService codeEditorService;
    @Value("${git.default.token}")
    private String defaultToken;
    private final Gson gson = new Gson();

    @PostMapping(value = "/projectTree")
    public Result<List<EditorPath>> projectTree(@RequestBody GitProjectReq body, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!StringUtils.isEmpty(body.getUrl())) {
            if (StringUtils.isEmpty(body.getToken())) {
                body.setToken(defaultToken);
            }
            return Result.success(codeEditorService.getProjectTree(body.getUrl(), body.getToken(), body.getBranch()));
        }
        List<EditorPath> tree = codeEditorService.getMockTree();
        return Result.success(tree);
    }

    @PostMapping(value = "/readFile")
    public Result<String> readFile(@RequestBody FileReq body, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!StringUtils.isEmpty(body.getUrl())) {
            if (StringUtils.isEmpty(body.getToken())) {
                body.setToken(defaultToken);
            }
            return Result.success(codeEditorService.getGitFile(body.getUrl(), body.getToken(), body.getPath(), body.getBranch()));
        }
        File p = new File(body.getPath());
        String str = codeEditorService.getMockFile(p.getName());
        return Result.success(str);
    }

    @PostMapping(value = "/updateFile")
    public Result<Boolean> updateFile(@RequestBody FileReq body, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!StringUtils.isEmpty(body.getUrl())) {
            if (StringUtils.isEmpty(body.getToken())) {
                body.setToken(defaultToken);
            }
            return Result.success(codeEditorService.updateGitFile(body.getUrl(), body.getToken(), body.getPath(), body.getBranch(), body.getContent(), body.getCommitMsg()));
        }
        File p = new File(body.getPath());
        codeEditorService.editMockFile(p.getName(), body.getContent());
        return Result.success(true);
    }

    @PostMapping(value = "/readScript")
    public Result<String> readScript(@RequestBody ScriptReq body, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        String str = codeEditorService.getScript(body.getScriptId());
        return Result.success(str);
    }

    @PostMapping(value = "/updateScript")
    public Result<Boolean> updateScript(@RequestBody ScriptReq body, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);

        return Result.success(codeEditorService.updateScript(body.getScriptId(), body.getContent()));
    }

    @PostMapping(value = "/runScript")
    public Result<Object> runScript(@RequestBody ScriptReq body, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        try {
            if (StringUtils.isEmpty(body.getContent())) {
                body.setContent(codeEditorService.getScript(body.getScriptId()));
            }
            Map<String, String> m = gson.fromJson(body.getParam(), new TypeToken<Map<String, String>>() {
            }.getType());
            List<Object> l = new ArrayList<>();
            l.add(m);
            l.add(new Context());
            return Result.success(codeEditorService.runGroovy(l.toArray(), body.getContent()));
        } catch (Exception e) {
            log.error("runScript",e);
            return Result.fromException(e);
        }
    }
}
