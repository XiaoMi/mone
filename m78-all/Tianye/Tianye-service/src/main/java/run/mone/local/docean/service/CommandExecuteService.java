package run.mone.local.docean.service;

import com.google.gson.Gson;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import run.mone.local.docean.dto.ExecCommandResult;
import run.mone.local.docean.service.tool.TerminalExecutorService;

import javax.annotation.Resource;

@Slf4j
@Service
public class CommandExecuteService {

    @Resource
    TerminalExecutorService executorService;

    /**
     * pull->commit -a
     * git提交当前目录下所有更新内容
     * @return
     */
    public Result gitCommit(){
        return gitCommitWithPath(null);
    }

    /**
     * pull->commit -a
     * git提交指定目录下所有更新内容
     * @return
     */
    public Result gitCommitWithPath(String workPath){

        Result<ExecCommandResult> gitPull = executorService.execCommand("git pull", workPath);
        if(gitPull.getCode() != 0 && gitPull.getData().getResultCode() != 0){
            log.info("execute command [ git pull ] fail! workPath : {}, execute result : {}", workPath, new Gson().toJson(gitPull));
            return Result.fail(GeneralCodes.InternalError,"execute command [ git pull ] fail!");
        }
        return executorService.execCommand("git commit -a -m \"update\"", workPath);
    }

    /**
     * pull->add->commit -a
     * git提交当前目录下所有更新内容，支持新建文件的提交
     * @return
     */
    public Result gitAddCommit(){
        return gitAddCommitWithPath(null);
    }

    /**
     * pull->add->commit -a
     * git提交指定目录下所有更新内容，支持新建文件的提交
     * @return
     */
    public Result gitAddCommitWithPath(String workPath){
        Result<ExecCommandResult> gitPull = executorService.execCommand("git pull", workPath);
        if(gitPull.getCode() != 0 && gitPull.getData().getResultCode() != 0){
            log.info("execute command [ git pull ] fail! workPath : {}, execute result : {}", workPath, new Gson().toJson(gitPull));
            return Result.fail(GeneralCodes.InternalError,"execute command [ git pull ] fail!");
        }

        Result<ExecCommandResult> gitAdd = executorService.execCommand("git add .", workPath);
        if(gitAdd.getCode() != 0 && gitAdd.getData().getResultCode() != 0){
            log.info("execute command [ git add . ] fail! workPath : {}, execute result : {}", workPath, new Gson().toJson(gitAdd));
            return Result.fail(GeneralCodes.InternalError,"execute command [ git add . ] fail!");
        }

        return executorService.execCommand("git commit -a -m \"update\"", workPath);
    }


}
