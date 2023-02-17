package com.xiaomi.mone.log.manager.service.impl;

import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.domain.EsCluster;
import com.xiaomi.mone.log.manager.domain.Tpc;
import com.xiaomi.mone.log.manager.mapper.MilogLogCountMapper;
import com.xiaomi.mone.log.manager.mapper.MilogLogstailMapper;
import com.xiaomi.mone.log.manager.model.dto.LogtailCollectTopDTO;
import com.xiaomi.mone.log.manager.model.dto.LogtailCollectTrendDTO;
import com.xiaomi.mone.log.manager.model.dto.SpaceCollectTopDTO;
import com.xiaomi.mone.log.manager.model.dto.SpaceCollectTrendDTO;
import com.xiaomi.mone.log.manager.model.pojo.LogCountDO;
import com.xiaomi.mone.log.manager.service.LogCountService;
import com.xiaomi.mone.log.utils.DateUtils;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.es.EsService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class LogCountServiceImpl implements LogCountService {
    @Resource
    private MilogLogCountMapper logCountMapper;

    @Resource
    private MilogLogstailMapper logtailMapper;

    @Resource
    private EsCluster esCluster;

    @Resource
    private Tpc tpc;

    private List<LogtailCollectTopDTO> logtailCollectTopList = new ArrayList<>();

    private Map<String, Map<Long, List<LogtailCollectTrendDTO>>> logtailCollectTrendMap = new HashMap<>();

    private List<SpaceCollectTopDTO> spaceCollectTopList = new ArrayList<>();

    private Map<Long, List<SpaceCollectTrendDTO>> spaceCollectTrendCache = new HashMap<>();

    /**
     * 日志收集排行
     * @return
     */
    @Override
    public Result<List<LogtailCollectTopDTO>> collectTop() {
        return Result.success(logtailCollectTopList);
    }

    /**
     * 日志收集趋势
     * @return
     * @param tailId
     */
    @Override
    public Result<List<LogtailCollectTrendDTO>> collectTrend(Long tailId) {
        String thisDay = DateUtils.getDaysAgo(1);
        if (!logtailCollectTrendMap.containsKey(thisDay) || !logtailCollectTrendMap.get(thisDay).containsKey(tailId)) {
            collectTrendCount(tailId);
        }
        return Result.success(logtailCollectTrendMap.get(thisDay).get(tailId));
    }

    @Override
    public void collectTopCount() {
        List<Map<String, Object>> res = logCountMapper.collectTopCount(DateUtils.getDaysAgo(7), DateUtils.getDaysAgo(1));
        List<LogtailCollectTopDTO> dtoList = new ArrayList();
        LogtailCollectTopDTO dto;
        for (Map<String, Object> count : res) {
            dto = new LogtailCollectTopDTO();
            dto.setTail(String.valueOf(count.get("tail")));
            dto.setNumber(getLogNumberFormat(Long.parseLong(count.get("number").toString())));
            dtoList.add(dto);
        }
        logtailCollectTopList = dtoList;
    }

    @Override
    public void collectTrendCount(Long tailId) {
        String yesterday = DateUtils.getDaysAgo(1);
        synchronized (this) {
            if (logtailCollectTrendMap.containsKey(yesterday) && logtailCollectTrendMap.get(yesterday).containsKey(tailId)) {
                return;
            }
            if (!logtailCollectTrendMap.containsKey(yesterday)) {
                logtailCollectTrendMap.remove(DateUtils.getDaysAgo(2));
                logtailCollectTrendMap.put(yesterday, new HashMap<>());
            }
        }
        synchronized (tailId) {
            if (logtailCollectTrendMap.get(yesterday).containsKey(tailId)) {
                return;
            }
            List<Map<String, Object>> resList = logCountMapper.collectTrend(DateUtils.getDaysAgo(7), DateUtils.getDaysAgo(1), tailId);
            List<LogtailCollectTrendDTO> trendlist = new ArrayList<>();
            LogtailCollectTrendDTO dto;
            for (Map<String, Object> res : resList) {
                dto = new LogtailCollectTrendDTO();
                dto.setDay(String.valueOf(res.get("day")).split("-")[2] + "日");
                dto.setNumber(String.valueOf(res.get("number")));
                dto.setShowNumber(getLogNumberFormat(Long.parseLong(String.valueOf(res.get("number")))));
                trendlist.add(dto);
            }
            logtailCollectTrendMap.get(yesterday).put(tailId, trendlist);
        }
    }

    @Override
    public void collectLogCount(String thisDay) throws IOException {
        log.info("统计日志开始");
        logCountMapper.deleteThisDay(thisDay);
        if (StringUtils.isEmpty(thisDay)) {
            thisDay = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }
        Long thisDayFirstMillisecond = DateUtils.getThisDayFirstMillisecond(thisDay);
        List<LogCountDO> logCountDOList = new ArrayList();
        LogCountDO logCountDO;
        EsService esService;
        Long total;
        String esIndex;
        List<Map<String, Object>> tailList = logtailMapper.getAllTailForCount();
        for (Map<String, Object> tail : tailList) {
            esIndex = String.valueOf(tail.get("es_index"));
            if (StringUtils.isEmpty(esIndex) || tail.get("es_cluster_id") == null) {
                total = 0l;
                esIndex = "";
            } else {
                esService = esCluster.getEsService(Long.parseLong(String.valueOf(tail.get("es_cluster_id"))));
                if (esService == null) {
                    log.warn("统计日志warn,tail:{}日志未统计，es客户端未生成", tail);
                    continue;
                }
                SearchSourceBuilder builder = new SearchSourceBuilder();
                BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
                boolQueryBuilder.filter(QueryBuilders.termQuery("tail", tail.get("tail")));
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("timestamp").from(thisDayFirstMillisecond).to(thisDayFirstMillisecond + DateUtils.dayms - 1));
                builder.query(boolQueryBuilder);
                // 统计
                CountRequest countRequest = new CountRequest();
                countRequest.indices(esIndex);
                countRequest.source(builder);
                total = esService.count(countRequest);
            }
            logCountDO = new LogCountDO();
            logCountDO.setTailId(Long.parseLong(String.valueOf(tail.get("id"))));
            logCountDO.setEsIndex(esIndex);
            logCountDO.setNumber(total);
            logCountDO.setDay(thisDay);
            logCountDOList.add(logCountDO);
        }
        Long res = logCountMapper.batchInsert(logCountDOList);
        log.info("统计日志结束,应统计{}行，共统计{}行", tailList.size(), res);
    }

    @Override
    public boolean isLogtailCountDone(String day) {
        Long logtailCountDone = logCountMapper.isLogtailCountDone(day);
        return !(logtailCountDone == 0L);
    }

    @Override
    public void deleteHistoryLogCount() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -70);
        String deleteBeforedDay = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        logCountMapper.deleteBeforeDay(deleteBeforedDay);
    }

    @Override
    public void collectLogDelete(String day) {
        logCountMapper.deleteThisDay(day);
    }

    @Override
    public void collectTrendRefresh() {
        logtailCollectTrendMap.clear();
    }

    private String getLogNumberFormat(long number) {
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        if (number >= 100000000) {
            return format.format((float)number / 100000000) + "亿";
        } else if (number >= 1000000) {
            return format.format((float)number / 1000000) + "百万";
        } else if(number >= 10000) {
            return format.format((float)number / 10000) + "万";
        } else {
            return number + "条";
        }
    }

    @Override
    public void showLogCountCache() {
        log.info("logTopList:{}", logtailCollectTopList);
        log.info("logTrendMap:{}", logtailCollectTrendMap);
    }

    @Override
    public Result<List<SpaceCollectTopDTO>> collectSpaceTop() {
        return Result.success(spaceCollectTopList);
    }

    @Override
    public void collectSpaceTopCount() {
        List<Map<String, Object>> spaceTopList = logCountMapper.collectSpaceCount(DateUtils.getDaysAgo(7), DateUtils.getDaysAgo(1));
        List<SpaceCollectTopDTO> dtoList = new ArrayList<>();
        SpaceCollectTopDTO dto;
        for (Map<String, Object> spaceTop : spaceTopList) {
            dto = new SpaceCollectTopDTO();
            dto.setSpaceName(String.valueOf(spaceTop.get("spaceName")));
            dto.setNumber(getLogNumberFormat(Long.parseLong(spaceTop.get("number").toString())));
            dto.setOrgName(tpc.getSpaceLastOrg(Long.parseLong(String.valueOf(spaceTop.get("spaceId")))));
            dtoList.add(dto);
        }
        spaceCollectTopList = dtoList;
    }

    @Override
    public void collectSpaceTrend() {
        List<Map<String, Object>> spaceTrendList = logCountMapper.collectSpaceTrend(DateUtils.getDaysAgo(7), DateUtils.getDaysAgo(1));
        Map<Long, List<SpaceCollectTrendDTO>> cache = new HashMap<>();
        List<SpaceCollectTrendDTO> dtoList = new ArrayList<>();
        SpaceCollectTrendDTO dto;
        Long lastSpaceId = Long.parseLong(String.valueOf(spaceTrendList.get(0).get("spaceId")));
        Long thisSpaceId;
        for (Map<String, Object> spaceTrend : spaceTrendList) {
            thisSpaceId = Long.parseLong(String.valueOf(spaceTrend.get("spaceId")));
            if (!thisSpaceId.equals(lastSpaceId)) {
                cache.put(lastSpaceId, dtoList);
                dtoList = new ArrayList<>();
            }
            dto = new SpaceCollectTrendDTO();
            dto.setSpaceName(String.valueOf(spaceTrend.get("spaceName")));
            dto.setNumber(String.valueOf(spaceTrend.get("number")));
            dto.setShowNumber(getLogNumberFormat(Long.parseLong(String.valueOf(spaceTrend.get("number")))));
            dto.setOrgName(tpc.getSpaceLastOrg(Long.parseLong(String.valueOf(spaceTrend.get("spaceId")))));
            dto.setDay(String.valueOf(spaceTrend.get("day")).split("-")[2] + "日");
            dtoList.add(dto);
            lastSpaceId = thisSpaceId;
        }
        spaceCollectTrendCache = cache;
    }

    @Override
    public Result<List<SpaceCollectTrendDTO>> spaceCollectTrend(Long spaceId) {
        return Result.success(spaceCollectTrendCache.get(spaceId));
    }

}
