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

import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.GitlabAccessToken;
import com.xiaomi.youpin.gwdash.dao.model.Project;
import com.xiaomi.youpin.gwdash.service.GitlabService;
import com.xiaomi.youpin.gwdash.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author tsingfu
 */
@RestController
@Slf4j
public class GitlabController {

    @Autowired
    private GitlabService gitlabService;

    @Autowired
    private LoginService loginService;

    @RequestMapping(value = "/api/gitlab/token/new", method = RequestMethod.POST)
    public Result<Boolean> createAccessToken(
            HttpServletRequest request,
            @RequestParam("name") String name,
            @RequestParam("token") String token,
            @RequestParam(value = "desc", defaultValue = "", required = false) String desc
    ) {
        SessionAccount sessionAccount = loginService.getAccountFromSession(request);
        return gitlabService.createAccessToken(sessionAccount.getUsername(), name, token, desc);
    }

    @RequestMapping(value = "/api/gitlab/token/edit", method = RequestMethod.POST)
    public Result<Boolean> editAccessToken(
            HttpServletRequest request,
            @RequestParam("id") long id,
            @RequestParam("name") String name,
            @RequestParam("token") String token,
            @RequestParam(value = "desc", defaultValue = "", required = false) String desc
    ) {
        SessionAccount sessionAccount = loginService.getAccountFromSession(request);
        return gitlabService.editAccessToken(sessionAccount.getUsername(), id, name, token, desc);
    }

    @RequestMapping(value = "/api/gitlab/token/del", method = RequestMethod.GET)
    public Result<Boolean> delAccessToken(
            HttpServletRequest request,
            @RequestParam("id") long id
    ) {
        SessionAccount sessionAccount = loginService.getAccountFromSession(request);
        return gitlabService.delAccessToken(sessionAccount.getUsername(), id);
    }

    @RequestMapping(value = "/api/gitlab/token/list", method = RequestMethod.GET)
    public Result<Map<String, Object>> getAccessToken(
            HttpServletRequest request,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize
    ) {
        SessionAccount sessionAccount = loginService.getAccountFromSession(request);
        return gitlabService.getAccessToken(sessionAccount.getUsername(), page, pageSize);
    }

    @RequestMapping(value = "/api/gitlab/branch", method = RequestMethod.GET)
    public Result<List<String>> getGitlabBranch (
            HttpServletRequest request,
            @RequestParam(value = "group") String group,
            @RequestParam(value = "name") String name
    ) throws IOException, GitAPIException {
        SessionAccount session = loginService.getAccountFromSession(request);
        GitlabAccessToken gitlabAccessToken = gitlabService.getAccessTokenByUsername(session.getUsername()).getData();
        if (null != gitlabAccessToken) {
            return new Result<>(1, "gitlab access token不存在，需要添加", null);
        }
        String path = System.getProperty("java.io.tmpdir") + File.separator + group + File.separator + name + File.separator;
        CredentialsProvider cp = new UsernamePasswordCredentialsProvider("test", "");
        Git git = Git.cloneRepository().setURI("youpin-server/mytest.git").setCredentialsProvider(cp).setDirectory(new File(path)).call();
        Collection<Ref> refs = git.lsRemote().setRemote("origin").call();
        List<String> list = new ArrayList<>();
        refs.stream().forEach(it -> {
            list.add(it.getName());
        });
        return Result.success(list);
    }
}
