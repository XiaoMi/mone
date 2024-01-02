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

package run.mone.mimeter.engine.client.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.gateway.dubbo.Dubbo;
import com.xiaomi.youpin.gateway.dubbo.MethodInfo;
import common.Util;
import org.apache.dubbo.rpc.RpcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import run.mone.mimeter.engine.agent.bo.data.CheckFilterConditionRes;
import run.mone.mimeter.engine.agent.bo.data.DubboData;
import run.mone.mimeter.engine.agent.bo.data.CommonReqInfo;
import run.mone.mimeter.engine.agent.bo.data.Result;
import run.mone.mimeter.engine.agent.bo.stat.DubboResultCheckInfo;
import run.mone.mimeter.engine.agent.bo.stat.SceneTotalCountContext;
import run.mone.mimeter.engine.agent.bo.task.HeraContextInfo;
import run.mone.mimeter.engine.agent.bo.task.Task;
import run.mone.mimeter.engine.agent.bo.task.TaskContext;
import run.mone.mimeter.engine.client.base.BaseClient;
import run.mone.mimeter.engine.common.CustomLogBuilder;
import run.mone.mimeter.engine.common.ResultCheck;
import run.mone.mimeter.engine.common.TraceUtil;
import run.mone.mimeter.engine.service.LoggerBuilder;

import javax.annotation.Resource;


import static common.Const.*;
import static run.mone.mimeter.dashboard.bo.sceneapi.DubboMavenVersionEnum.ORIGIN_MAVEN;
import static run.mone.mimeter.engine.service.MetricsService.recordApiRpsMetrics;
import static run.mone.mimeter.engine.service.MetricsService.recordTpsAndRtMetrics;

/**
 * @author goodjava@qq.com
 * @author dongzhenxing
 * @date 2022/5/19
 */
@Component(name = "dubboMClient")
public class DubboClient extends BaseClient {
    @Resource
    private Dubbo dubbo;

    private final Gson gson = new Gson();

    private static final Logger log = LoggerFactory.getLogger(DubboClient.class);

    @Resource
    private LoggerBuilder loggerBuilder;

    @Override
    public Result call(Task task, TaskContext context, CommonReqInfo commonReqInfo, SceneTotalCountContext totalCountContext) {
        //目前的逻辑是有一个发生错误了,后边就不在调用了
        if (context.isError()) {
            return Result.cancel();
        }
        DubboData dubboData = task.getDubboData();
        //检查是否满足忽略的条件
        Result result = new Result();
        //检查是否满足忽略的条件
        CheckFilterConditionRes checkFilterConditionRes = ResultCheck.checkFilterConditionSatisfy(context.getAttachments(), dubboData.getFilterCondition());
        if (checkFilterConditionRes.isMatch()) {
            result.setOk(false);
            result.setData("passed");
            result.setCode(0);
            result.setTriggerFilterCondition(checkFilterConditionRes.getTriggerFilterCondition());
            return result;
        }
        HeraContextInfo heraContextInfo = task.getHeraContextInfo();

        Integer sceneId = 0;
        Integer serialId = 0;
        String reportId = "";
        Integer apiId = 0;

        if (!task.isDebug()) {
            sceneId = heraContextInfo.getSceneId();
            serialId = heraContextInfo.getSerialLinkId();
            reportId = heraContextInfo.getTaskFlag();
            apiId = heraContextInfo.getSceneApiId();
        }

        //前置过滤器,用于根据业务需求自定义处理转换入参
        commonReqInfo = this.doPreFilter(task,context, commonReqInfo);

        MethodInfo mi = new MethodInfo();
        if (dubboData.getServiceName().startsWith("providers:")) {
            mi.setServiceName(dubboData.getServiceName().substring("provider:".length() + 1));
        } else {
            mi.setServiceName(dubboData.getServiceName());
        }
        mi.setMethodName(dubboData.getMethodName());
        if (dubboData.getGroup() != null) {
            mi.setGroup(dubboData.getGroup());
        }
        if (dubboData.getVersion() != null) {
            mi.setVersion(dubboData.getVersion());
        }
        if (!ORIGIN_MAVEN.mavenVersion.equals(dubboData.getMavenVersion())) {
            mi.setProto("json");
        }

        mi.setArgsProto("gson");
        RpcContext.getContext().setAttachment("gson_generic_args", "true");
        mi.setProtoVersion("v1");
        mi.setTimeout(task.getTimeout());
        mi.setParameterTypes(dubboData.getRequestParamTypeList().toArray(new String[0]));
        Object[] params;
        try {
            params = gson.fromJson(commonReqInfo.getParamJson(), new TypeToken<Object[]>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            return Result.cancel();
        }
        mi.setArgs(params);

        //携带Attachments
        setAttachment(dubboData);

        //生成traceid
        String trace = trace();

        //带入trace标记
        injectHeraContext(task.isDebug(), task.getHeraContextInfo());

        Object res;
        String errorInfo = null;

        int code = 500;
        long start = 0;
        try {
            start = System.currentTimeMillis();
            res = dubbo.call(mi);
            code = 0;
        } catch (Exception exception) {
            errorInfo = exception.getMessage();
            log.error("[DubboClient] call invoke exception, task id: " + task.getId() + ", " + "scene id: " + sceneId +
                    ", report id: " + reportId + ", " + "serial id: " + serialId +
                    ", submit type: " + task.getSubmitTaskType() + ", task type: " + task.getType().code + ", " + errorInfo);
            res = errorInfo;
        }
        //后置过滤器,用于根据业务需求自定义处理转换出参
        this.doPostFilter(task,res);

        // check point, error analysis
        DubboResultCheckInfo checkInfo = ResultCheck.checkDubboResult(dubboData, res, code, task.isDebug());

        if (!checkInfo.isOk()) {
            log.error("dubbo call error:{}", errorInfo);
            context.setError(true);
        }
        long elapsed = System.currentTimeMillis() - start;

        if (!task.isDebug()) {
            //打点rps
            recordApiRpsMetrics(new String[]{String.valueOf(sceneId), reportId, String.valueOf(serialId),
                    task.getType().name(), dubboData.getServiceName(), dubboData.getMethodName(), String.valueOf(apiId), String.valueOf(checkInfo.isOk())});
            ResultCheck.recordRpsCount(apiId, totalCountContext);

            // check point, error analysis
            String[] labelVals = new String[]{String.valueOf(sceneId), reportId, String.valueOf(serialId),
                    task.getType().name(), String.valueOf(code), dubboData.getServiceName(), dubboData.getMethodName(),
                    String.valueOf(apiId), String.valueOf(checkInfo.isOk())};

            recordTpsAndRtMetrics(elapsed, labelVals, true);

            //rt统计
            ResultCheck.recordApiRtAndCount(apiId, elapsed, checkInfo.isOk(), totalCountContext, true);

            //错误统计
            ResultCheck.dubboApiErrorAnalysis(apiId, checkInfo, totalCountContext);

            //采样日志
            if (sendLog && ((boolean) context.getAttachments().getOrDefault(TASK_CTX_RECORD_LOG, false) || !checkInfo.isOk())) {
                loggerBuilder.getLogger().info(CustomLogBuilder.buildApiLog(!checkInfo.isOk(), task.getId(), API_TYPE_DUBBO, dubboData.getServiceName(), dubboData.getMethodName(),
                        elapsed, code, sceneId, serialId, reportId, apiId, gson.toJson(res), commonReqInfo.getParamJson(), "", "", trace, errorInfo));
            }
        }
        result.setCode(code);
        result.setOk(checkInfo.isOk());
        result.setData(gson.toJson(res));
        result.setCommonReqInfo(commonReqInfo);
        result.setRt(elapsed);
        result.setSize(res.toString().getBytes().length);
        return result;
    }


    /**
     * 设置attachments
     */
    private void setAttachment(DubboData dubboData) {
        RpcContext.getContext().setAttachments(dubboData.getAttachments());
    }

    /**
     * 插入trace id
     */
    private String trace() {
        String traceId = TraceUtil.traceId();
        RpcContext.getContext().getAttachments().put(DUBBO_TRACE_HEADER_KEY, "00-" + traceId + "-" + TraceUtil.spanId() + "-01");
        return traceId;
    }

    /**
     * 注入tracing信息
     */
    private void injectHeraContext(boolean isDebug, HeraContextInfo heraContextInfo) {
        if (!isDebug && checkHeraInfoParam(heraContextInfo)) {
            StringBuilder sb = new StringBuilder();
            String sceneTask = heraContextInfo.getSceneId() + "_" + heraContextInfo.getTaskFlag();

            sb.append(HERA_SCENE_TASK).append(":").append(sceneTask).append(";");
            sb.append(HERA_SERIAL_LINK).append(":").append(heraContextInfo.getSerialLinkId()).append(";");
            sb.append(HERA_API_ID).append(":").append(heraContextInfo.getSceneApiId()).append(";");

            RpcContext.getContext().getAttachments().put(HEAR_HEADER_KEY, sb.toString());
        }
    }

    private boolean checkHeraInfoParam(HeraContextInfo heraContextInfo) {
        return heraContextInfo.getTaskFlag() != null && heraContextInfo.getSceneId() != null
                && heraContextInfo.getSerialLinkId() != null;
    }

}
