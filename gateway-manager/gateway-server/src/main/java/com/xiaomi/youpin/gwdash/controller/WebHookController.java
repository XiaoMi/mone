//package com.xiaomi.youpin.gwdash.controller;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.nacos.common.util.Md5Utils;
//import com.xiaomi.data.push.redis.Redis;
//import com.xiaomi.youpin.gwdash.bo.AutoStartContextDTO;
//import com.xiaomi.youpin.gwdash.bo.SessionAccount;
//import com.xiaomi.youpin.gwdash.common.Consts;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.dao.model.Project;
//import com.xiaomi.youpin.gwdash.dao.model.ProjectEnv;
//import com.xiaomi.youpin.gwdash.dao.model.ProjectPipeline;
//import com.xiaomi.youpin.gwdash.service.PipelineService;
//import com.xiaomi.youpin.gwdash.service.ProjectEnvService;
//import com.xiaomi.youpin.gwdash.service.ProjectService;
//import com.xiaomi.youpin.gwdash.service.UserService;
//import com.xiaomi.youpin.hermes.bo.response.Account;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.env.Environment;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.concurrent.ExecutorService;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//@Slf4j
//@RestController
//@RequestMapping("/open/")
//public class WebHookController {
//
//
//    @Autowired
//    private ProjectService projectService;
//
//    @Autowired
//    private ProjectEnvService projectEnvService;
//
//    @Autowired
//    private UserService userService;
//
//
//    @Autowired
//    private PipelineService pipelineService;
//
//    @Autowired
//    private ExecutorService executorService;
//
//    @Autowired
//    private Redis redis;
//
//    @Autowired
//    private Environment environment;
//
//    /**
//     * 米效接收git的push回调接口
//     * @param requestBody 该json结构自行查找 https://git.n.xiaomi.com/help/user/project/integrations/webhooks
//     * @param projectId
//     * @param request
//     * @return
//     */
//    @PostMapping("/api/mione/web_hook")
//    public Result<Void> gitlabWebHook(@RequestBody JSONObject requestBody, @RequestParam Long projectId, HttpServletRequest request){
//        Result<Void> voidResult = new Result<>();
//        String serverType = environment.getProperty("server.type");
//        if(serverType != null && (serverType.equals("c3") || serverType.equals("c4") || serverType.equals("intra") || serverType.equals("preview"))){
//            return voidResult;
//        }
//        //验证是否正常调用
//        String token = request.getHeader("X-Gitlab-Token");
//        if(StringUtils.isBlank(token) || projectId == null || projectId <= 0){
//            return voidResult;
//        }
//        String md5Token = Md5Utils.getMD5(String.valueOf(projectId).getBytes());
//        if(!md5Token.equals(token)){
//            return voidResult;
//        }
//
//        //验证事件类型
//        String eventKind = requestBody.getString("object_kind");
//        if(!"push".equals(eventKind)){
//            return voidResult;
//        }
//        //获取提交人的信息，并验证其权限
//        JSONArray commits = requestBody.getJSONArray("commits");
//        if(commits == null){
//            //对于测试的提交不予处理
//            return voidResult;
//        }
//        Project project = projectService.getProjectById(projectId).getData();
//        if(project == null){
//            return voidResult;
//        }
//        //判定最后一次提交人push是否是项目成员
//        JSONObject commit = commits.getJSONObject(commits.size()-1);
//        String commitId = commit.getString("id");
//        JSONObject author = commit.getJSONObject("author");
//        if(commitId == null || author == null || author.getString("name") == null){
//            return voidResult;
//        }
//        String committerEmail = author.getString("email");
//        if(committerEmail == null || !committerEmail.contains("@")){
//            return voidResult;
//        }
//        String committerName = committerEmail.split("@")[0];
//        Account account = userService.queryUserByName(committerName);
//        if(account == null || !projectService.isOwner(projectId,account.getId())){
//            return voidResult;
//        }
//
//        //判断分支是否是我们要自动化的分支
//        String branch = subBranch(requestBody.getString("ref"));
//        if(StringUtils.isBlank(branch)){
//            return voidResult;
//        }
//        List<ProjectEnv> projectEnvs = projectEnvService.getProjectEnv(projectId);
//        if(CollectionUtils.isEmpty(projectEnvs)){
//            return voidResult;
//        }
//        //提交的分支是否命中项目下某一个环境指定的分支的
//        List<Long> projectEnvIds = projectEnvs.stream().filter(projectEnv -> Objects.equals(projectEnv.getBranch(), branch) && projectEnv.isAutoDeploy()).map(ProjectEnv::getId).collect(Collectors.toList());
//        if(CollectionUtils.isEmpty(projectEnvIds)){
//            return voidResult;
//        }
//        Map<Long, ProjectEnv> projectEnvMap = projectEnvs.stream().collect(Collectors.toMap(ProjectEnv::getId, Function.identity(), (p1, p2) -> p1));
//        //信息收集好了，开始自动话流程
//        executorService.execute(()->{
//            SessionAccount sessionAccount = new SessionAccount(account.getId(), account.getUserName(), account.getName(), account.getToken(), 0, account.getGid(), new ArrayList<>(), account.getGidInfos());
//            sessionAccount.setEmail(committerEmail);
//            projectEnvIds.forEach(projectEnvId->{
//                Result<ProjectPipeline> projectPipelineResult = pipelineService.startPipeline(projectId, projectEnvId, 0, commitId, sessionAccount, false, true);
//                if(projectPipelineResult != null && projectPipelineResult.isSuccess()){
//                    AutoStartContextDTO context = new AutoStartContextDTO();
//                    context.setAutoBuild(Boolean.TRUE.toString());
//                    context.setAutoDeploy(Boolean.valueOf(projectEnvMap.get(projectEnvId).isAutoDeploy()).toString());
//                    context.setProjectId(projectId);
//                    context.setProjectEnvId(projectEnvId);
//                    context.setSessionAccount(sessionAccount);
//                    context.setCommitMessage(commit.getString("title"));
//                    context.setProjectName(project.getName());
//                    redis.set(Consts.AUTO_START_PREFIX+"p"+projectPipelineResult.getData().getId(),context,3600000);
//                    pipelineService.startCodeCheck(projectId,projectEnvId,sessionAccount.getUsername());
//                }
//            });
//        });
//        return voidResult;
//    }
//
//    private String subBranch(String ref){
//        if(ref != null && ref.startsWith("refs/heads/")){
//            return ref.substring(11);
//        }
//        return ref;
//    }
//
//
//}
