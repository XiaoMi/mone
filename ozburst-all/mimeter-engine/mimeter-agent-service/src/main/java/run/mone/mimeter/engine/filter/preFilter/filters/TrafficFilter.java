package run.mone.mimeter.engine.filter.preFilter.filters;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.traffic.BasicTraffic;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.traffic.BasicTrafficList;
import common.Util;
import lombok.extern.slf4j.Slf4j;
import run.mone.mimeter.engine.agent.bo.data.CommonReqInfo;
import run.mone.mimeter.engine.agent.bo.data.HttpData;
import run.mone.mimeter.engine.agent.bo.task.Task;
import run.mone.mimeter.engine.agent.bo.task.TaskContext;
import run.mone.mimeter.engine.agent.bo.task.TaskType;
import run.mone.mimeter.engine.filter.common.BasePreFilter;
import run.mone.mimeter.engine.filter.common.FilterOrder;
import run.mone.mimeter.engine.filter.dto.BasicTrafficListDTO;
import run.mone.mimeter.engine.filter.preFilter.PreFilter;
import run.mone.mimeter.engine.filter.preFilter.PreFilterAnno;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static common.Const.HTTP_GET;
import static common.Const.HTTP_POST;

/**
 * @author dongzhenxing
 * 录制流量参数过滤器
 */
@Component
@FilterOrder(4)
@PreFilterAnno
@Slf4j
public class TrafficFilter extends PreFilter {

    public static final Gson gson = Util.getGson();

    /**
     * 用于暂存本 agent 当前所用的流量数据
     */
    private static final ConcurrentHashMap<Integer, BasicTrafficListDTO> trafficCache = new ConcurrentHashMap<>();

    @Override
    public CommonReqInfo doFilter(Task task, TaskContext context,CommonReqInfo commonReqInfo, BasePreFilter filter) {
        //非http任务或未启用流量数据，直接略过
        if (task.getType() != TaskType.http || !task.getHttpData().isEnableTraffic()) {
            return filter.doFilter(task,context, commonReqInfo);
        }
        //启用流量数据,替换header、params
        HttpData httpData = task.getHttpData();
        if (!trafficCache.containsKey(httpData.getTrafficConfId())) {
            //没有缓存流量数据，直接略过
            return filter.doFilter(task,context, commonReqInfo);
        }
        BasicTrafficListDTO basicTrafficListDTO = trafficCache.get(httpData.getTrafficConfId());

        //空数组，跳过
        if (basicTrafficListDTO.getList().size() == 0){
            return filter.doFilter(task,context, commonReqInfo);
        }

        //取模，循环读取
        BasicTraffic traffic = basicTrafficListDTO.getList().get(basicTrafficListDTO.getPointer().intValue() % basicTrafficListDTO.getList().size());
        //取完后移动游标
        trafficCache.get(httpData.getTrafficConfId()).getPointer().add(1);

        //增加请求头
        commonReqInfo.getHeaders().putAll(traffic.getOriginHeaders());

        //替换请求体
        if (httpData.getMethod().equalsIgnoreCase(HTTP_POST)) {
            String originBody = traffic.getOrginBody();
            commonReqInfo.setParamJson(originBody);
        } else if (httpData.getMethod().equalsIgnoreCase(HTTP_GET)) {
            String originBody = traffic.getOriginQueryString();
            try {
                Map<String, String> queryMap = gson.fromJson(originBody, new TypeToken<Map<String, String>>() {
                }.getType());
                commonReqInfo.setQueryParamMap(queryMap);
            } catch (JsonSyntaxException e) {
                log.error("TrafficFilter doFilter error query body");
            }
        }
        return filter.doFilter(task, context,commonReqInfo);
    }

    /**
     * 更新暂存加载到的traffic数据
     */
    public static void updateTrafficCache(List<BasicTrafficList> trafficLists) {
        trafficLists.forEach(trafficList -> {
            BasicTrafficListDTO dto = new BasicTrafficListDTO();
            dto.setRecordingConfigId(trafficList.getRecordingConfigId());
            dto.setList(trafficList.getList());
            dto.setPage(trafficList.getPage());
            dto.setPageSize(trafficList.getPageSize());
            dto.setTotal(trafficList.getTotal());
            trafficCache.put(trafficList.getRecordingConfigId(), dto);
        });
    }

    /**
     * 压测结束后清理流量数据缓存
     */
    public static void cleanTrafficCache(List<Integer> trafficConfIdList) {
        trafficConfIdList.forEach(trafficCache::remove);
    }
}
