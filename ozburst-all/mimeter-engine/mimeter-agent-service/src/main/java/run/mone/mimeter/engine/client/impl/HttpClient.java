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

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.hera.trace.annotation.Trace;
import com.xiaomi.youpin.docean.anno.Component;
import common.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import run.mone.mimeter.engine.agent.bo.data.CheckFilterConditionRes;
import run.mone.mimeter.engine.agent.bo.data.HttpData;
import run.mone.mimeter.engine.agent.bo.data.CommonReqInfo;
import run.mone.mimeter.engine.agent.bo.data.Result;
import run.mone.mimeter.engine.agent.bo.stat.HttpResultCheckInfo;
import run.mone.mimeter.engine.agent.bo.stat.SceneTotalCountContext;
import run.mone.mimeter.engine.agent.bo.task.HeraContextInfo;
import run.mone.mimeter.engine.agent.bo.task.Task;
import run.mone.mimeter.engine.agent.bo.task.TaskContext;
import run.mone.mimeter.engine.client.base.BaseClient;
import run.mone.mimeter.engine.common.CustomLogBuilder;
import run.mone.mimeter.engine.common.ErrorCode;
import run.mone.mimeter.engine.common.ResultCheck;
import run.mone.mimeter.engine.common.TraceUtil;
import run.mone.mimeter.engine.service.LoggerBuilder;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static common.Const.*;
import static run.mone.mimeter.engine.service.MetricsService.recordApiRpsMetrics;
import static run.mone.mimeter.engine.service.MetricsService.recordTpsAndRtMetrics;

/**
 * @author goodjava@qq.com
 * @author dongzhenxing
 * @date 2023/3/23
 */
@Component(name = "httpMClient")
public class HttpClient extends BaseClient {
    private static final Logger log = LoggerFactory.getLogger(HttpClient.class);

    private final Gson gson = Util.getGson();
    @Resource
    private LoggerBuilder loggerBuilder;

    @Override
    @Trace
    public Result call(Task task, TaskContext context, CommonReqInfo commonReqInfo, SceneTotalCountContext totalCountContext) {
        try {
            //目前的逻辑是有一个发生错误了,后边就不在调用了
            if (context.isError()) {
                log.info("http call cancel:{}", task.getId());
                return Result.cancel();
            }

            HttpData httpData = task.getHttpData();

            Result result = new Result();
            //检查是否满足忽略的条件
            CheckFilterConditionRes checkFilterConditionRes = ResultCheck.checkFilterConditionSatisfy(context.getAttachments(), httpData.getFilterCondition());
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

            String url = httpData.getUrl();
            String method = httpData.getMethod();
            int timeout = Math.max(httpData.getTimeout(), 10);

            HttpResult res = null;

            //前置过滤器,用于根据业务需求自定义处理转换入参
            log.debug("do pre filter before,common req:{}",commonReqInfo);

            commonReqInfo = this.doPreFilter(task,context, commonReqInfo);

            log.debug("do pre filter after,common req:{}",commonReqInfo);
            if(StringUtils.isNotBlank(commonReqInfo.getDebugUrl())){
                url = commonReqInfo.getDebugUrl();
            }
            String paramBody = "";
            String encoding = "utf-8";
            if (commonReqInfo.getHeaders().getOrDefault(Const.DISABLE_URL_ENCODE, "false").equals("true")) {
                encoding = "";
            }
            String errorInfo = "";
            boolean needLog = (boolean) context.getAttachments().getOrDefault(TASK_CTX_RECORD_LOG, false);
            boolean logIgnore = false;
            TpsRecord needRecordTps = new TpsRecord(true);
            String traceId = "";
            long start = 0;
            try {
                //注入trace信息
                traceId = this.trace(commonReqInfo.getHeaders());
                //注入探针信息
                injectHeraContext(task.isDebug(), heraContextInfo, commonReqInfo.getHeaders());
                List<String> headers = new ArrayList<>();
                commonReqInfo.getHeaders().forEach((key, value) -> {
                    headers.add(key);
                    headers.add(value);
                });
                if (method.equalsIgnoreCase(HTTP_GET)) {
                    if (needLog) {
                        paramBody = gson.toJson(commonReqInfo.getQueryParamMap());
                    }
                    start = System.currentTimeMillis();
                    log.debug("http get api url:{}.headers:{}", url, headers);

                    res = HttpClientV6.httpGet(needRecordTps, url, headers, commonReqInfo.getQueryParamMap(), encoding, timeout);
                } else if (method.equalsIgnoreCase(HTTP_POST)) {
                    //json格式为 对象 {} 或基本类型 "d"/1/true...
                    //表单参数格式
                    if (httpData.getContentType().equals(CONTENT_TYPE_APP_FORM) || httpData.getContentType().equals(CONTENT_TYPE_APP_FORM2)) {
                        if (needLog) {
                            paramBody = gson.toJson(commonReqInfo.getQueryParamMap());
                        }
                        start = System.currentTimeMillis();
                        res = HttpClientV6.httpPost(needRecordTps, url, commonReqInfo.getHeaders(), commonReqInfo.getQueryParamMap(), encoding, timeout);
                    } else {
                        paramBody = commonReqInfo.getParamJson();
                        //application/json格式
                        start = System.currentTimeMillis();
                        log.debug("do call before,common req:{}",commonReqInfo);
                        res = HttpClientV6.postRt(needRecordTps, url, commonReqInfo.getParamJson().getBytes(StandardCharsets.UTF_8), commonReqInfo.getHeaders(), timeout);
                    }
                }
            } catch (Throwable ex) {
                needRecordTps.setNeedRecordTps(false);
                if (ex.getMessage() != null) {
                    errorInfo = ex.getMessage();
                } else {
                    errorInfo = ex.getCause().getMessage();
                }
                log.error("[HttpClient] call invoke exception, task id: " + task.getId() + ", " + "scene id: " + sceneId +
                        ", report id: " + reportId + ", " + "serial id: " + serialId +
                        ", submit type: " + task.getSubmitTaskType() + ", task type: " + task.getType().code + " " + errorInfo);

                res = new HttpResult(ErrorCode.ERROR_500.code, errorInfo, Maps.newHashMap());
            } finally {
                long elapsed = (System.currentTimeMillis() - start);
                int respCode = res == null ? ErrorCode.ERROR_500.code : res.code;

                //后置过滤器,用于根据业务需求自定义处理转换出参
                this.doPostFilter(task, res);

                // check point, error analysis
                HttpResultCheckInfo checkInfo = ResultCheck.checkHttpResult(httpData, res, task.getSuccessCode(), task.isDebug());

                if (!checkInfo.isOk()) {
                    if (method.equalsIgnoreCase(HTTP_GET) || httpData.getContentType().equals(CONTENT_TYPE_APP_FORM) || httpData.getContentType().equals(CONTENT_TYPE_APP_FORM2)) {
                        paramBody = gson.toJson(commonReqInfo.getQueryParamMap());
                    }
                    log.error("http call error:{}", res.code);
                    context.setError(true);
                }
                if (!task.isDebug()) {
                    if (!needRecordTps.isNeedRecordTps()) {
                        //httpclient 请求报错
                        //检查是否为连接超时
                        assert res != null;
                        if (judgeIfConnectTimeout(res)) {
                            //统计丢失连接数
                            ResultCheck.recordLossConnCount(totalCountContext);
                            logIgnore = true;
                        } else {
                            collectData(task, sceneId, reportId, serialId, url, method, apiId, checkInfo, totalCountContext, elapsed, respCode, needRecordTps);
                        }
                    } else {
                        collectData(task, sceneId, reportId, serialId, url, method, apiId, checkInfo, totalCountContext, elapsed, respCode, needRecordTps);
                    }
                }
                result.setCode(res.code);
                result.setOk(checkInfo.isOk());
                result.setData(res.content);
                result.setRespHeaders(res.getHeaders());
                result.setCommonReqInfo(commonReqInfo);
                if (!checkInfo.isOk() && checkInfo.getTriggerCpInfo() != null) {
                    //触发的检查点信息
                    result.setTriggerCp(checkInfo.getTriggerCpInfo());
                }
                result.setRt(elapsed);
                result.setSize(res.content.getBytes().length);
                //采样日志
                if (sendLog && (needLog || (!checkInfo.isOk()) && !logIgnore)) {
                    loggerBuilder.getLogger().info(CustomLogBuilder.buildApiLog(!checkInfo.isOk(), task.getId(), API_TYPE_HTTP, url, method, elapsed, result.getCode(), sceneId, serialId,
                            reportId, apiId, Objects.equals("Read timed out",res.content)?(res.content + " ( start = "+start ):res.content, paramBody, gson.toJson(commonReqInfo.getHeaders()), gson.toJson(res.getHeaders()), traceId, errorInfo));
                }
            }
            return result;
        } finally {
            log.debug("finish:{}:{}", task.getId(), task.getHttpData().getUrl());
        }
    }

    /**
     * 统计打点数据
     */
    private void collectData(Task task, Integer sceneId, String reportId, Integer serialId
            , String url, String method, Integer apiId, HttpResultCheckInfo checkInfo,
                             SceneTotalCountContext totalCountContext, long elapsed, Integer respCode,
                             TpsRecord needRecordTps) {
        //打点rps
        recordApiRpsMetrics(new String[]{String.valueOf(sceneId), reportId, String.valueOf(serialId),
                task.getType().name(), url, method, String.valueOf(apiId), String.valueOf(checkInfo.isOk())});
        ResultCheck.recordRpsCount(apiId, totalCountContext);

        //打点 tps
        recordTpsAndRtMetrics(elapsed, new String[]{String.valueOf(sceneId), reportId, String.valueOf(serialId),
                task.getType().name(), String.valueOf(respCode), url, method, String.valueOf(apiId),
                String.valueOf(checkInfo.isOk())}, needRecordTps.isNeedRecordTps());
        //rt 次数统计
        ResultCheck.recordApiRtAndCount(apiId, elapsed, checkInfo.isOk(), totalCountContext, needRecordTps.isNeedRecordTps());
        //错误统计
        ResultCheck.httpApiErrorAnalysis(apiId, checkInfo, totalCountContext, task.getSuccessCode());
    }

    /**
     * 插入trace id
     */
    private String trace(Map<String, String> headers) {
        String traceId = TraceUtil.traceId();
        headers.put(DUBBO_TRACE_HEADER_KEY, "00-" + traceId + "-" + TraceUtil.spanId() + "-01");
        return traceId;
    }

    /**
     * 注入tracing信息
     */
    private void injectHeraContext(boolean isDebug, HeraContextInfo heraContextInfo, Map<String, String> headers) {
        if (!isDebug && checkHeraInfoParam(heraContextInfo)) {
            StringBuilder sb = new StringBuilder();
            String sceneTask = heraContextInfo.getSceneId() + "_" + heraContextInfo.getTaskFlag();

            sb.append(HERA_SCENE_TASK).append(":").append(sceneTask).append(";");
            sb.append(HERA_SERIAL_LINK).append(":").append(heraContextInfo.getSerialLinkId()).append(";");
            sb.append(HERA_API_ID).append(":").append(heraContextInfo.getSceneApiId()).append(";");

            headers.put(Const.HEAR_HEADER_KEY, sb.toString());
        }
    }

    private boolean checkHeraInfoParam(HeraContextInfo heraContextInfo) {
        return heraContextInfo.getTaskFlag() != null && heraContextInfo.getSceneId() != null
                && heraContextInfo.getSerialLinkId() != null;
    }

    /**
     * 是否连接超时，是则作为丢失连接
     */
    private boolean judgeIfConnectTimeout(HttpResult res) {
        if (res.code != 500) {
            return false;
        }
        return res.content.equals("Connect timed out") || res.content.equals("Connection timed out");
    }

}
