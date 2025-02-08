package run.mone.m78.service.service.feature.router.asyncCallTask;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.mone.m78.api.bo.feature.router.asyncCallTask.SubmitAsyncTaskReq;
import run.mone.m78.api.bo.feature.router.asyncCallTask.SubmitAsyncTaskRes;
import run.mone.m78.api.bo.feature.router.asyncCallTask.GetAsyncTaskRes;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.dao.entity.M78OpenapiAsyncTaskPo;
import run.mone.m78.service.dao.mapper.M78OpenapiAsyncTaskMapper;
import run.mone.m78.service.service.feature.router.FeatureRouterService;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import static run.mone.m78.api.bo.feature.router.asyncCallTask.CallbackStatusEnum.NOT_STARTED;
import static run.mone.m78.api.bo.feature.router.asyncCallTask.TaskStatusEnum.*;
import static run.mone.m78.api.bo.feature.router.asyncCallTask.TypeEnum.PROBOT_EXEC;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_NOT_FOUND;

@Service
@Slf4j

/**
 * M78OpenapiAsyncTaskService类负责处理异步任务的提交和查询操作。
 * 该类继承自ServiceImpl，并使用Spring的@Service注解进行标注，表明其为一个服务类。
 * 主要功能包括：
 * 1. 提交Probot任务并新建异步任务。
 * 2. 根据任务ID查询异步任务信息。
 *
 * 该类依赖于FeatureRouterService来执行具体的Probot任务，并通过Gson进行JSON处理。
 * 提交任务时，会生成一个唯一的任务ID，并将任务信息保存到数据库中。
 * 查询任务时，根据任务ID从数据库中获取任务信息，并返回任务的状态和输出。
 *
 * 使用@Slf4j注解进行日志记录，方便调试和错误追踪。
 */

public class M78OpenapiAsyncTaskService extends ServiceImpl<M78OpenapiAsyncTaskMapper, M78OpenapiAsyncTaskPo> {

    private static Gson gson = GsonUtils.gson;

    @Resource
    private FeatureRouterService featureRouterService;

    /**
     * 提交Probot任务并新建异步任务
     *
     * @param req 提交任务的请求参数，包含任务相关信息
     * @return 提交结果，包含任务ID和任务状态
     */
    //提交Probot任务，新建createAsyncTask
    public Result<SubmitAsyncTaskRes> submit(JsonObject req) {

        log.info("[M78OpenapiAsyncTaskService], Submit task with request: {}", req);

        String taskId = UUID.randomUUID().toString();
        try {
            //新建
            SubmitAsyncTaskReq submitAsyncTaskReq = getSubmitAsyncTaskReq(req);
            M78OpenapiAsyncTaskPo task = M78OpenapiAsyncTaskPo.builder()
                    .type(submitAsyncTaskReq.getType())
                    .relateId(submitAsyncTaskReq.getRelateId())
                  //  .inputs(gson.toJson(submitAsyncTaskReq.getInputs()))
                    .inputs(JSON.toJSONString(submitAsyncTaskReq.getInputs()))
                    .taskId(taskId)
                    .taskStatus(RUNNING.getCode())
                    .callbackUrl(submitAsyncTaskReq.getCallbackUrl())
                    .callbackStatus(NOT_STARTED.getCode())
                    .invokeStartTime(System.currentTimeMillis())
                    .invokeUserName(submitAsyncTaskReq.getInvokeUserName())
                    .build();
            createAsyncTask(task);

            //提交
            submitProbotTask(task, req);

        } catch (Throwable e) {
            log.error("[M78OpenapiAsyncTaskService], Submit task error: {}", e.getMessage());
            SubmitAsyncTaskRes res = SubmitAsyncTaskRes.builder().taskId(taskId).taskStatus(FAILURE.getDesc()).message(e.getMessage()).build();
            return Result.success(res);
        }

        SubmitAsyncTaskRes res = SubmitAsyncTaskRes.builder().taskId(taskId).taskStatus(RUNNING.getDesc()).build();
        return Result.success(res);
    }

    /**
     * 根据任务ID查询异步任务信息
     *
     * @param taskId 任务ID
     * @return 查询结果，包含任务状态和输出信息
     */
    //根据taskid查询,返回结果是GetAsyncTaskRes
    public Result<GetAsyncTaskRes> getTaskById(String taskId) {
        M78OpenapiAsyncTaskPo task = getOne(new QueryWrapper().eq("task_id", taskId));
        if (task == null) {
            GetAsyncTaskRes res = GetAsyncTaskRes.builder()
                    .taskId(taskId)
                    .taskStatus(NOT_EXIST.getDesc())
                    .message("taskId is not exist")
                    .build();
            return Result.success(res);
        }
        GetAsyncTaskRes res = GetAsyncTaskRes.builder()
                .taskId(task.getTaskId())
                .taskStatus(getDescByCode(task.getTaskStatus()))
                .output(task.getOutputs())
                .build();
        return Result.success(res);
    }

    private void submitProbotTask(M78OpenapiAsyncTaskPo task, JsonObject req) {

        Callable<Result<JsonObject>> action = () -> featureRouterService.executeProbot(req);

        Consumer<Result<JsonObject>> onResult = result -> {
            //更新task
            if (result == null || result.getCode() != 0) {
                task.setTaskStatus(FAILURE.getCode());
                task.setInvokeEndTime(System.currentTimeMillis());
                task.setOutputs((result == null) ? "null" : gson.toJson(result));
                updateAsyncTask(task);
            } else {
                task.setTaskStatus(SUCCESS.getCode());
                task.setInvokeEndTime(System.currentTimeMillis());
                task.setOutputs(gson.toJson(result));
                updateAsyncTask(task);
            }

            //进行回调
        };

        Consumer<String> onFailure = errorMsg -> {
            //更新task
            task.setTaskStatus(FAILURE.getCode());
            task.setInvokeEndTime(System.currentTimeMillis());
            task.setOutputs(errorMsg);
            updateAsyncTask(task);

            //进行回调
        };

        TaskExecutor.submit(task.getTaskId(), action, onResult, onFailure);
    }


    //新建一个，入参是M78OpenapiAsyncTaskPo类型的
    private void createAsyncTask(M78OpenapiAsyncTaskPo task) {
        save(task);
    }

    //更新，入参是M78OpenapiAsyncTaskPo类型的
    private boolean updateAsyncTask(M78OpenapiAsyncTaskPo task) {
        return updateById(task);
    }

    private SubmitAsyncTaskReq getSubmitAsyncTaskReq(JsonObject req) {
        SubmitAsyncTaskReq.SubmitAsyncTaskReqBuilder builder = SubmitAsyncTaskReq.builder();
        builder.type(PROBOT_EXEC.getCode());
        if (req.has("botId")) builder.relateId(req.get("botId").getAsLong());
        if (req.has("input")) builder.inputs(req.get("input"));
        if (req.has("callbackUrl")) builder.callbackUrl(req.get("callbackUrl").getAsString());
        if (req.has("userName")) builder.invokeUserName(req.get("userName").getAsString());
        return builder.build();
    }

}
