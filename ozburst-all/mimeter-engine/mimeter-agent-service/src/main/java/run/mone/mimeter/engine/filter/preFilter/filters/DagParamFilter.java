package run.mone.mimeter.engine.filter.preFilter.filters;

import com.google.gson.Gson;
import com.xiaomi.youpin.docean.anno.Component;
import common.Replacer;
import common.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import run.mone.mimeter.engine.agent.bo.data.CommonReqInfo;
import run.mone.mimeter.engine.agent.bo.data.ParamType;
import run.mone.mimeter.engine.agent.bo.data.ParamTypeEnum;
import run.mone.mimeter.engine.agent.bo.task.Task;
import run.mone.mimeter.engine.agent.bo.task.TaskContext;
import run.mone.mimeter.engine.agent.bo.task.TaskType;
import run.mone.mimeter.engine.filter.common.BasePreFilter;
import run.mone.mimeter.engine.filter.common.FilterOrder;
import run.mone.mimeter.engine.filter.preFilter.PreFilter;
import run.mone.mimeter.engine.filter.preFilter.PreFilterAnno;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dongzhenxing
 * x5 Dag参数替换过滤器
 */
@Component
@FilterOrder(1)
@PreFilterAnno
@Slf4j
public class DagParamFilter extends PreFilter {

    private static final Gson gson = Util.getGson();

    @Override
    public CommonReqInfo doFilter(Task task, TaskContext context, CommonReqInfo commonReqInfo, BasePreFilter filter) {

        //非http任务，直接略过
        if (task.getType() != TaskType.http && task.getType() != TaskType.dubbo) {
            return filter.doFilter(task, context, commonReqInfo);
        }
        Map<String, Object> contextMap = context.getAttachments();
        //替换参数
        updateParam(task, commonReqInfo, contextMap);
        return filter.doFilter(task, context, commonReqInfo);
    }

    /**
     * 用于上游节点接口动态更新本接口实际参数
     *
     * @ paramIndex 参数索引位置
     * @ paramName  参数名 若paramName 为例：${name} 类型，则直接替换
     * @ value      上游传下来的实际参数值
     */
    private void updateParam(Task task, CommonReqInfo reqInfo, Map<String, Object> contextMap) {
        if (contextMap.get("replacerMap") == null) {
            return;
        }
        Map<String, List<Replacer>> replacerMap = (Map<String, List<Replacer>>) contextMap.get("replacerMap");
        replacerMap.values().forEach(replacers -> {
            replacers.stream().filter(replacer -> replacer.getTaskId() == task.getId()).forEach(replacer -> {
                if (replacer.getParamName().startsWith("${")) {
                    try {
                        //post请求 的 json参数
                        String valStr = replacer.getValue().toString();

                        if (!replacer.isForceStr()) {
                            if (NumberUtils.isNumber(valStr)) {
                                if (isNumeric(valStr)) {
                                    replacer.setValue(Long.parseLong(valStr));
                                } else {
                                    replacer.setValue(Double.parseDouble(valStr));
                                }
                            } else {
                                replacer.setValue(valStr);
                            }
                        }

                        reqInfo.setParamJson(Util.Parser.parse$(Util.getElKey(replacer.getParamName()).getKey(), reqInfo.getParamJson(), replacer.getValue()));
                    } catch (Exception e) {
                        log.error("updateParam param after,body:{},time:{},error:{}", reqInfo.getParamJson(), System.currentTimeMillis(), e);
                    }
                } else {
                    if (task.getHttpData().getTypes().size() > replacer.getParamIndex()) {
                        ParamType type = task.getHttpData().getTypes().get(replacer.getParamIndex());
                        //对象类型
                        if (type.getTypeEnum().equals(ParamTypeEnum.pojo)) {
                            ConcurrentHashMap<String, Object> params = (ConcurrentHashMap<String, Object>) task.getHttpData().getParams().get(replacer.getParamIndex());
                            params.put(replacer.getParamName(), replacer.getValue());
                        }
                        //基本类型
                        if (type.getTypeEnum().equals(ParamTypeEnum.primary)) {
                            task.getHttpData().getParams().set(replacer.getParamIndex(), replacer.getValue());
                        }
                        reqInfo.setQueryParamMap(task.getHttpData().httpGetTmpParams(task.getHttpData().getParams()));
                        if (task.isDebug()) {
                            reqInfo.setParamJson(gson.toJson(reqInfo.getQueryParamMap()));
                        }
                    }
                }
            });
        });
    }

    private static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57)
                return false;
        }
        return true;
    }

}
