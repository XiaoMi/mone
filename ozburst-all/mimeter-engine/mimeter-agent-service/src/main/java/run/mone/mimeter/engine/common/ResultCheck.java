package run.mone.mimeter.engine.common;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.xiaomi.data.push.antlr.expr.Expr;
import common.HttpResult;
import common.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import run.mone.mimeter.dashboard.bo.sceneapi.OutputOriginEnum;
import run.mone.mimeter.engine.agent.bo.data.BaseData;
import run.mone.mimeter.engine.agent.bo.data.CheckPointInfo;
import run.mone.mimeter.engine.agent.bo.data.CheckFilterConditionRes;
import run.mone.mimeter.engine.agent.bo.stat.DubboResultCheckInfo;
import run.mone.mimeter.engine.agent.bo.stat.HttpResultCheckInfo;
import run.mone.mimeter.engine.agent.bo.data.OutputParam;
import run.mone.mimeter.engine.agent.bo.stat.SceneTotalCountContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;

import static common.Const.*;

@Slf4j
public class ResultCheck {

    public static final Gson gson = Util.getGson();

    /**
     * 错误分析，检查点
     */
    public static HttpResultCheckInfo checkHttpResult(BaseData baseData, HttpResult res, String successCode, boolean debug) {
        HttpResultCheckInfo checkInfo = new HttpResultCheckInfo();
        if (baseData.getCheckPointInfoList() == null || baseData.getCheckPointInfoList().size() == 0) {
            //没有配置检查点，以状态码为主
            List<String> succCodes = new ArrayList<>();
            if (successCode != null) {
                succCodes.addAll(Arrays.stream(successCode.split(",")).toList());
            } else {
                succCodes.add("200");
                succCodes.add("0");
            }
            if (!succCodes.contains(String.valueOf(res.code))) {
                checkInfo.setOk(false);
                checkInfo.setCheckPointId(0);
                checkInfo.setHttpStatusCode(String.valueOf(res.code));
                return checkInfo;
            } else {
                checkInfo.setOk(true);
            }
        } else {
            //以检查点为主
            CheckPointInfo triggerCp = ResultCheck.checkPointSatisfyHttp(baseData.getOutputParams(), baseData.getCheckPointInfoList(), res);
            if (triggerCp != null && triggerCp.getId() != null && triggerCp.getId() != 0) {
                //触发检查点
                checkInfo.setOk(false);
                checkInfo.setCheckPointId(triggerCp.getId());
                if (debug) {
                    checkInfo.setTriggerCpInfo(gson.toJson(triggerCp));
                }
                return checkInfo;
            } else {
                if (debug && triggerCp != null) {
                    checkInfo.setOk(false);
                    checkInfo.setTriggerCpInfo(gson.toJson(triggerCp));
                }
                if (triggerCp == null) {
                    checkInfo.setOk(true);
                }
            }
            checkInfo.setHttpStatusCode(String.valueOf(res.code));
        }
        return checkInfo;
    }

    /**
     * dubbo 错误判断，检查点
     */
    public static DubboResultCheckInfo checkDubboResult(BaseData baseData, Object res, int code, boolean debug) {
        DubboResultCheckInfo checkInfo = new DubboResultCheckInfo();

        if (baseData.getCheckPointInfoList() == null || baseData.getCheckPointInfoList().size() == 0) {
            //未配置检查点，默认已code为准，即调用没有报错就算成功

            if (code != 0) {
                checkInfo.setOk(false);
                checkInfo.setCheckPointId(0);
                return checkInfo;
            } else {
                checkInfo.setOk(true);
            }
        } else {
            CheckPointInfo triggerCp = ResultCheck.checkPointSatisfyDubbo(baseData.getOutputParams(), baseData.getCheckPointInfoList(), res);
            if (triggerCp != null && triggerCp.getId() != null && triggerCp.getId() != 0) {
                //触发检查点
                checkInfo.setOk(false);
                checkInfo.setCheckPointId(triggerCp.getId());
                if (debug) {
                    checkInfo.setTriggerCpInfo(gson.toJson(triggerCp));
                }
            } else {
                checkInfo.setOk(true);
            }
        }
        return checkInfo;
    }

    /**
     * dubbo请求错误分析统计
     */
    public static void dubboApiErrorAnalysis(int apiId, DubboResultCheckInfo checkInfo, SceneTotalCountContext errorContext) {
        if (checkInfo.isOk()) {
            errorContext.getTotalSuccReq().increment();
            return;
        }
        //总错误次数+1
        errorContext.getTotalErrReq().increment();
        String checkPointId = String.valueOf(checkInfo.getCheckPointId());
        if (!checkPointId.equals("0")) {
            // 检查点触发
            String checkPointKey = ERR_CHECKPOINT_PREFIX + checkPointId;
            if (errorContext.getErrCounterMap().containsKey(checkPointKey)) {
                //已存在该检查点错误
                if (errorContext.getErrCounterMap().get(checkPointKey).containsKey(apiId)) {
                    //已记录该接口,+1
                    errorContext.getErrCounterMap().get(checkPointKey).get(apiId).increment();
                } else {
                    //初始化1次
                    LongAdder newAdder = new LongAdder();
                    newAdder.increment();
                    errorContext.getErrCounterMap().get(checkPointKey).put(apiId, newAdder);
                }
            } else {
                //初始化1次
                ConcurrentHashMap<Integer, LongAdder> tmpMap = new ConcurrentHashMap<>();

                LongAdder newAdder = new LongAdder();
                newAdder.increment();
                tmpMap.put(apiId, newAdder);
                //该检查点错误尚未记录
                errorContext.getErrCounterMap().put(checkPointKey, tmpMap);
            }
        } else {
            //调用异常触发
            String statusCodeKey = "dubbo_call_fail";
            if (errorContext.getErrCounterMap().containsKey(statusCodeKey)) {
                //已存在该错误码
                if (errorContext.getErrCounterMap().get(statusCodeKey).containsKey(apiId)) {
                    //已记录该接口,+1
                    errorContext.getErrCounterMap().get(statusCodeKey).get(apiId).increment();
                } else {
                    //初始化1次
                    LongAdder newAdder = new LongAdder();
                    newAdder.increment();
                    errorContext.getErrCounterMap().get(statusCodeKey).put(apiId, newAdder);
                }
            } else {
                //该错误码尚未记录
                //初始化1次
                ConcurrentHashMap<Integer, LongAdder> tmpMap = new ConcurrentHashMap<>();
                LongAdder newAdder = new LongAdder();
                newAdder.increment();
                tmpMap.put(apiId, newAdder);
                errorContext.getErrCounterMap().put(statusCodeKey, tmpMap);
            }
        }
    }

    /**
     * http请求错误分析统计
     */
    public static void httpApiErrorAnalysis(int apiId, HttpResultCheckInfo checkInfo, SceneTotalCountContext errorContext, String successCode) {
        if (checkInfo.isOk()) {
            errorContext.getTotalSuccReq().increment();
            return;
        }
        List<String> succCodes = new ArrayList<>();
        if (successCode != null) {
            succCodes.addAll(Arrays.stream(successCode.split(",")).toList());
        } else {
            succCodes.add("200");
            succCodes.add("0");
        }
        //总错误次数+1
        errorContext.getTotalErrReq().increment();
        String statusCode = checkInfo.getHttpStatusCode();
        String checkPointId = String.valueOf(checkInfo.getCheckPointId());
        if (!checkPointId.equals("0")) {
            // 检查点触发
            String checkPointKey = ERR_CHECKPOINT_PREFIX + checkPointId;
            if (errorContext.getErrCounterMap().containsKey(checkPointKey)) {
                //已存在该检查点错误
                if (errorContext.getErrCounterMap().get(checkPointKey).containsKey(apiId)) {
                    //已记录该接口,+1
                    errorContext.getErrCounterMap().get(checkPointKey).get(apiId).increment();
                } else {
                    //初始化1次
                    LongAdder newAdder = new LongAdder();
                    newAdder.increment();
                    errorContext.getErrCounterMap().get(checkPointKey).put(apiId, newAdder);
                }
            } else {
                //初始化1次
                ConcurrentHashMap<Integer, LongAdder> tmpMap = new ConcurrentHashMap<>();

                LongAdder newAdder = new LongAdder();
                newAdder.increment();
                tmpMap.put(apiId, newAdder);
                //该检查点错误尚未记录
                errorContext.getErrCounterMap().put(checkPointKey, tmpMap);
            }
        } else if (!succCodes.contains(statusCode)) {
            //http 状态码触发
            String statusCodeKey = ERR_STATUS_CODE_PREFIX + statusCode;
            if (errorContext.getErrCounterMap().containsKey(statusCodeKey)) {
                //已存在该错误码
                if (errorContext.getErrCounterMap().get(statusCodeKey).containsKey(apiId)) {
                    //已记录该接口,+1
                    errorContext.getErrCounterMap().get(statusCodeKey).get(apiId).increment();
                } else {
                    //初始化1次
                    LongAdder newAdder = new LongAdder();
                    newAdder.increment();
                    errorContext.getErrCounterMap().get(statusCodeKey).put(apiId, newAdder);
                }
            } else {
                //该错误码尚未记录
                //初始化1次
                ConcurrentHashMap<Integer, LongAdder> tmpMap = new ConcurrentHashMap<>();
                LongAdder newAdder = new LongAdder();
                newAdder.increment();
                tmpMap.put(apiId, newAdder);
                errorContext.getErrCounterMap().put(statusCodeKey, tmpMap);
            }
        }
    }

    /**
     * 是否满足检查点规则 dubbo
     */
    public static CheckPointInfo checkPointSatisfyDubbo(List<OutputParam> outputParams, List<CheckPointInfo> checkPointInfos, Object res) {
        if (checkPointInfos == null || checkPointInfos.size() == 0) {
            return null;
        }
        AtomicBoolean ok = new AtomicBoolean(true);
        for (CheckPointInfo checkPointInfo :
                checkPointInfos) {
            //dubbo接口的检查点只有出参校验类型
            if (checkPointInfo.getCheckType() == OUTPUT_CODE) {
                String outputKey = checkPointInfo.getCheckObj();
                OutputParam opParam = outputParams.stream().filter(outputParam -> outputParam.getParamName().equals(outputKey)).findFirst().get();
                try {
                    Object value;
                    if (opParam.getOrigin() == OutputOriginEnum.BODY_TXT.code) {
                        value = res.toString();
                    } else {
                        if (opParam.getParseExpr().contains("|")) {
                            //存在该标识符说明为list中取值
                            //表达式 例如：params.json().get(data).get(goodIds)|0.string
                            try {
                                String[] exprArr = opParam.getParseExpr().split("\\|", 2);
                                value = Expr.params(gson.toJson(res), exprArr[0]);
                                JsonArray valList = (JsonArray) value;
                                if (exprArr[1].contains(".")) {
                                    String[] indexAndType = exprArr[1].split("\\.", 2);
                                    value = Util.getListValByType(valList, Integer.parseInt(indexAndType[0]), indexAndType[1]);
                                } else {
                                    value = valList.get(Integer.parseInt(exprArr[1]));
                                }
                            } catch (Exception e) {
                                value = "";
                                log.error("dubbo check point parse get list val error,expr:{},cause by:{}", opParam.getParseExpr(), e.getMessage());
                            }
                        } else {
                            try {
                                value = Expr.params(gson.toJson(res), opParam.getParseExpr());
                            } catch (Exception e) {
                                value = "";
                            }
                        }
                    }
                    ok.set(checkOutput(value, checkPointInfo.getCheckCondition(), checkPointInfo.getCheckContent()));
                } catch (Exception e) {
                    ok.set(false);
                }
            }
            if (!ok.get()) {
                //一个不满足即错误
                //一个不满足即错误
                CheckPointInfo triggerCp = new CheckPointInfo();
                BeanUtils.copyProperties(checkPointInfo, triggerCp);
                return triggerCp;
            }
        }

        return null;
    }

    /**
     * 是否满足过滤规则
     * 条件list中关系为 条件与 and，即满足所有条件则过滤
     * 单条中以 | 间隔为 条件或 or
     */
    public static CheckFilterConditionRes checkFilterConditionSatisfy(Map<String, Object> attachments, List<CheckPointInfo> filterConditionList) {
        CheckFilterConditionRes checkFilterConditionRes = new CheckFilterConditionRes();
        if (filterConditionList == null || filterConditionList.size() == 0) {
            checkFilterConditionRes.setMatch(false);
            return checkFilterConditionRes;
        }
        log.debug("checkFilterConditionSatisfy attachments:{},filterCondition :{}", attachments, filterConditionList.get(0));
        //若
        //起始置为匹配，条件list中有不满足的则置为false
        AtomicBoolean ok = new AtomicBoolean(true);
        for (CheckPointInfo checkPointInfo : filterConditionList) {
            if (checkPointInfo.getCheckType() == OUTPUT_CODE) {
                String outputKey = checkPointInfo.getCheckObj();
                Object value = attachments.get(outputKey);
                //取不到上游值，直接抛出不匹配
                if (value == null) {
                    checkFilterConditionRes.setMatch(false);
                    return checkFilterConditionRes;
                }
                try {
                    //若取值比较不满足,直接抛出不匹配
                    if (!checkOutput(value, checkPointInfo.getCheckCondition(), checkPointInfo.getCheckContent())){
                        ok.set(false);
                        break;
                    }
                } catch (Exception e) {
                    ok.set(false);
                    break;
                }
            }
        }
        //满足所有条件，需要过滤该接口
        if (ok.get()) {
            checkFilterConditionRes.setMatch(ok.get());
            checkFilterConditionRes.setTriggerFilterCondition(gson.toJson(filterConditionList));
        }
        return checkFilterConditionRes;
    }

    /**
     * 是否满足检查点规则 http
     */
    public static CheckPointInfo checkPointSatisfyHttp(List<OutputParam> outputParams, List<CheckPointInfo> checkPointInfos, HttpResult res) {
        if (checkPointInfos == null || checkPointInfos.size() == 0) {
            return null;
        }
        AtomicBoolean ok = new AtomicBoolean(true);
        for (CheckPointInfo checkPointInfo :
                checkPointInfos) {
            switch (checkPointInfo.getCheckType()) {
                case STATUS_CODE ->
                        ok.set(checkStatusCode(res.code, checkPointInfo.getCheckCondition(), checkPointInfo.getCheckContent()));
                case HEADER_CODE -> {
                    String headerValue = res.getHeader(checkPointInfo.getCheckObj());
                    ok.set(checkHeader(checkPointInfo.getCheckObj(), checkPointInfo.getCheckCondition(), headerValue));
                }
                case OUTPUT_CODE -> {
                    String outputKey = checkPointInfo.getCheckObj();
                    OutputParam opParam = outputParams.stream().filter(outputParam -> outputParam.getParamName().equals(outputKey)).findFirst().get();
                    Object value;
                    try {
                        if (opParam.getOrigin() == OutputOriginEnum.BODY_TXT.code) {
                            value = res.content;
                        } else {
                            if (opParam.getParseExpr().contains("|")) {
                                //存在该标识符说明为list中取值
                                //表达式 例如：params.json().get(data).get(goodIds)|0.string
                                try {
                                    String[] exprArr = opParam.getParseExpr().split("\\|", 2);
                                    value = Expr.params(res.content, exprArr[0]);
                                    JsonArray valList = (JsonArray) value;
                                    if (exprArr[1].contains(".")) {
                                        String[] indexAndType = exprArr[1].split("\\.", 2);
                                        value = Util.getListValByType(valList, Integer.parseInt(indexAndType[0]), indexAndType[1]);
                                    } else {
                                        value = valList.get(Integer.parseInt(exprArr[1]));
                                    }
                                } catch (Exception e) {
                                    value = "";
                                    log.error("http check point parse get list val error,expr:{},cause by:{}", opParam.getParseExpr(), e.getMessage());
                                }
                            } else {
                                value = Expr.params(res.content, opParam.getParseExpr());
                            }
                        }
                        ok.set(checkOutput(value, checkPointInfo.getCheckCondition(), checkPointInfo.getCheckContent()));
                    } catch (Exception e) {
                        ok.set(false);
                    }
                }
            }
            if (!ok.get()) {
                //一个不满足即错误
                CheckPointInfo triggerCp = new CheckPointInfo();
                BeanUtils.copyProperties(checkPointInfo, triggerCp);
                return triggerCp;
            }
        }
        return null;
    }

    /**
     * 取值比较
     * @param opValue 上游实际值
     * @param condition 比较条件
     * @param targetStr 目标值
     */
    public static boolean checkOutput(Object opValue, int condition, String targetStr) {
        String v = opValue.toString();
        String[] targetStrArr;
        if (targetStr.contains("|")) {
            targetStrArr = targetStr.split("\\|");
        } else {
            targetStrArr = new String[]{targetStr};
        }
        boolean match = false;

        switch (condition) {
            case EQ -> {
                //=
                for (String s : targetStrArr) {
                    if (v.equals(s)) {
                        match = true;
                        break;
                    }
                }
            }
            case NOT_EQ -> {
                //!=
                for (String s : targetStrArr) {
                    if (!v.equals(s)) {
                        match = true;
                        break;
                    }
                }
            }
            case BIGGER -> {
                // >
                for (String s : targetStrArr) {
                    if ((Integer.parseInt(v) > Integer.parseInt(s))) {
                        match = true;
                        break;
                    }
                }
            }
            case BIGGER_AND_EQ -> {
                // >=
                for (String s : targetStrArr) {
                    if ((Integer.parseInt(v) >= Integer.parseInt(s))) {
                        match = true;
                        break;
                    }
                }
            }
            case SMLLER -> {
                // <
                for (String s : targetStrArr) {
                    if ((Integer.parseInt(v) < Integer.parseInt(s))) {
                        match = true;
                        break;
                    }
                }
            }
            case SMALLER_AND_EQ -> {
                // <=
                for (String s : targetStrArr) {
                    if ((Integer.parseInt(v) <= Integer.parseInt(s))) {
                        match = true;
                        break;
                    }
                }
            }
            case CONTAIN -> {
                //包含
                for (String s : targetStrArr) {
                    if (v.contains(s)) {
                        match = true;
                        break;
                    }
                }
            }
            case NOT_CONTAIN -> {
                //不包含
                for (String s : targetStrArr) {
                    if (!v.contains(s)) {
                        match = true;
                        break;
                    }
                }
            }
        }
        return match;
    }

    public static boolean checkStatusCode(int httpStatus, int condition, String targetStr) {

        String[] targetStrArr;
        if (targetStr.contains("|")) {
            targetStrArr = targetStr.split("\\|");
        } else {
            targetStrArr = new String[]{targetStr};
        }
        boolean match = false;

        switch (condition) {
            case EQ -> {
                //=
                for (String target : targetStrArr) {
                    if (httpStatus == Integer.parseInt(target)) {
                        match = true;
                        break;
                    }
                }
            }
            case NOT_EQ -> {
                //=
                for (String target : targetStrArr) {
                    if (httpStatus != Integer.parseInt(target)) {
                        match = true;
                        break;
                    }
                }
            }
            case BIGGER -> {
                // >
                for (String target : targetStrArr) {
                    if ((httpStatus > Integer.parseInt(target))) {
                        match = true;
                        break;
                    }
                }
            }
            case BIGGER_AND_EQ -> {
                // >=
                for (String target : targetStrArr) {
                    if ((httpStatus >= Integer.parseInt(target))) {
                        match = true;
                        break;
                    }
                }
            }
            case SMLLER -> {
                // <
                for (String target : targetStrArr) {
                    if ((httpStatus < Integer.parseInt(target))) {
                        match = true;
                        break;
                    }
                }
            }
            case SMALLER_AND_EQ -> {
                // <=
                for (String target : targetStrArr) {
                    if ((httpStatus <= Integer.parseInt(target))) {
                        match = true;
                        break;
                    }
                }
            }
            case CONTAIN -> {
                //包含
                String statusCodeStr = String.valueOf(httpStatus);
                for (String target : targetStrArr) {
                    if (statusCodeStr.contains(target)) {
                        match = true;
                        break;
                    }
                }
            }
            case NOT_CONTAIN -> {
                //不包含
                String statusCodeStr = String.valueOf(httpStatus);
                for (String target : targetStrArr) {
                    if (!statusCodeStr.contains(target)) {
                        match = true;
                        break;
                    }
                }
            }
        }
        return match;
    }

    public static boolean checkHeader(String headerVal, int condition, String targetStr) {

        String[] targetStrArr;
        if (targetStr.contains("|")) {
            targetStrArr = targetStr.split("\\|");
        } else {
            targetStrArr = new String[]{targetStr};
        }
        boolean match = false;

        switch (condition) {
            case EQ -> {
                //=
                for (String target : targetStrArr) {
                    if (headerVal.equals(target)) {
                        match = true;
                        break;
                    }
                }
            }
            case NOT_EQ -> {
                //=
                for (String target : targetStrArr) {
                    if (!headerVal.equals(target)) {
                        match = true;
                        break;
                    }
                }
            }
            case CONTAIN -> {
                //包含
                for (String target : targetStrArr) {
                    if (headerVal.contains(target)) {
                        match = true;
                        break;
                    }
                }
            }
            case NOT_CONTAIN -> {
                //不包含
                for (String target : targetStrArr) {
                    if (!headerVal.contains(target)) {
                        match = true;
                        break;
                    }
                }
            }
        }
        return match;
    }

    public static void recordLossConnCount(SceneTotalCountContext totalCountContext) {
//        总请求次数+1
//        totalCountContext.getTotalReq().increment();
        //总丢失连接次数+1
        totalCountContext.getLossConnNum().increment();
    }

    /**
     * 统计记录接口rt数据
     */
    public static void recordApiRtAndCount(int apiId, long rt, boolean success, SceneTotalCountContext totalCountContext, boolean recordTps) {

        //tps 请求数+1
        if (recordTps) {
            totalCountContext.getTmpTpsCounter().increment();
            totalCountContext.getTotalTCount().increment();
        }

        ConcurrentHashMap<Integer, ConcurrentHashMap<String, CopyOnWriteArrayList<Integer>>> apiRtAndTpsMap = totalCountContext.getApiRtMap();
        if (!apiRtAndTpsMap.containsKey(apiId)) {
            //首次执行该接口
            ConcurrentHashMap<String, CopyOnWriteArrayList<Integer>> map = new ConcurrentHashMap<>();
            CopyOnWriteArrayList<Integer> rtList = new CopyOnWriteArrayList<>();
            rtList.add((int) rt);
            map.put(RT_LIST, rtList);
            apiRtAndTpsMap.put(apiId, map);

        } else {
            //已存在该接口记录,相关rt记录必然已初始化
            apiRtAndTpsMap.get(apiId).get(RT_LIST).add((int) rt);
        }

        //接口请求次数统计  实际上是tps
        ConcurrentHashMap<Integer, ConcurrentHashMap<String, LongAdder>> apiCountMap = totalCountContext.getApiCountMap();
        //总数+1
        if (apiCountMap.get(apiId) == null) {
            log.error("api Id API_REQ_TOTAL_T is null:{}", apiId);
        } else {
            if (recordTps) {
                apiCountMap.get(apiId).get(API_REQ_TOTAL_T).increment();
            }
            if (success) {
                apiCountMap.get(apiId).get(API_REQ_SUCC).increment();
            } else {
                apiCountMap.get(apiId).get(API_REQ_FAIL).increment();
            }
        }
    }

    public static void recordRpsCount(int apiId, SceneTotalCountContext totalCountContext) {
        //总次数+1
        //tps 请求数+1
        totalCountContext.getTmpRpsCounter().increment();

        totalCountContext.getTotalReq().increment();
        //接口请求次数统计  实际上是rps
        ConcurrentHashMap<Integer, ConcurrentHashMap<String, LongAdder>> apiCountMap = totalCountContext.getApiCountMap();
        if (!apiCountMap.containsKey(apiId)) {
            //首次统计该接口
            ConcurrentHashMap<String, LongAdder> map = new ConcurrentHashMap<>();
            LongAdder total = new LongAdder();
            total.increment();
            map.put(API_REQ_TOTAL_R, total);
            map.put(API_REQ_TOTAL_T, new LongAdder());
            map.put(API_REQ_SUCC, new LongAdder());
            map.put(API_REQ_FAIL, new LongAdder());

            apiCountMap.put(apiId, map);
        } else {
            //总数+1
            apiCountMap.get(apiId).get(API_REQ_TOTAL_R).increment();
        }
    }

}
