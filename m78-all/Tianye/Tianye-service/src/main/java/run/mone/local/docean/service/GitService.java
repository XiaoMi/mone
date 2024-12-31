package run.mone.local.docean.service;

import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import run.mone.m78.api.GitLabService;
import run.mone.m78.api.bo.gitlab.GitLabReq;
import run.mone.m78.api.bo.gitlab.GitTreeItem;

import java.util.List;

/**
 * @author wmin
 * @date 2024/2/26
 */
@Slf4j
//@Service
public class GitService {

    @Reference(interfaceClass = GitLabService.class, group = "${dubbo.group}", version = "${dubbo.version}", timeout = 30000,check = false)
    private GitLabService gitLabService;

    public Result<String> getFileContent(GitLabReq gitLabReq){
        return gitLabService.getFileContent(gitLabReq);
    }


    public Result<String> parseProjectJavaFile(GitLabReq gitLabReq){
        return gitLabService.parseProjectJavaFile(gitLabReq);
    }

    public Result<List<GitTreeItem>> getProjectStructureTree(GitLabReq gitLabReq){
        return gitLabService.getProjectStructureTree(gitLabReq);
    }


}
