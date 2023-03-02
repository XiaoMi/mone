package com.xiaomi.mone.log.manager.domain;

import com.google.common.collect.Lists;
import com.xiaomi.mone.log.api.model.vo.UpdateLogProcessCmd;
import com.xiaomi.mone.log.manager.model.convert.LogProcessConvert;
import com.xiaomi.mone.log.manager.dao.MilogLogTailDao;
import com.xiaomi.mone.log.manager.model.cache.LogCellectProcessCache;
import com.xiaomi.mone.log.manager.model.dto.AgentLogProcessDTO;
import com.xiaomi.mone.log.manager.model.dto.TailLogProcessDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTailDo;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.common.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LogProcess {

    @Resource
    private MilogLogTailDao logtailDao;

    private Map<String, List<LogCellectProcessCache>> logCollectProcessMap = new ConcurrentHashMap();

    private Map<String, List<UpdateLogProcessCmd.CollectDetail>> tailProgressMap = new ConcurrentHashMap<>(256);

    private static final Integer MAX_INTERRUPT_TIME = 10;

    /**
     * 更新日志收集进度
     *
     * @param cmd
     */
    public void updateLogProcess(UpdateLogProcessCmd cmd) {
        log.debug("[LogProcess.updateLogProcess] cmd:{} ", cmd);
        if (cmd == null || StringUtils.isEmpty(cmd.getIp())) {
            return;
        }
        if (cmd.getCollectList() == null || cmd.getCollectList().isEmpty()) {
            logCollectProcessMap.put(cmd.getIp(), new ArrayList<>());
        } else {
            logCollectProcessMap.put(cmd.getIp(), LogProcessConvert.INSTANCE.cmdToCacheList(cmd.getCollectList()));
        }
        tailProgressMap.put(cmd.getIp(), cmd.getCollectList());
    }

    /**
     * 获取agent日志收集进度
     *
     * @param ip
     * @return
     */
    public List<AgentLogProcessDTO> getAgentLogProcess(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return new ArrayList<>();
        }
        List<LogCellectProcessCache> collectDetailList = logCollectProcessMap.get(ip);
        if (collectDetailList == null || collectDetailList.isEmpty()) {
            return new ArrayList<>();
        }
        List<AgentLogProcessDTO> dtoList = LogProcessConvert.INSTANCE.cacheToAgentDTOList(collectDetailList);
        return dtoList;
    }

    /**
     * 获取agent日志收集进度
     *
     * @param ip
     * @return
     */
    public List<TailLogProcessDTO> getIpLogProcess(String ip, MilogLogTailDo tail) {
        if (StringUtils.isEmpty(ip)) {
            return new ArrayList<>();
        }
        List<LogCellectProcessCache> logProcessCacheList = logCollectProcessMap.get(ip);
        if (logProcessCacheList == null || logProcessCacheList.isEmpty()) {
            return new ArrayList<>();
        }
        List<TailLogProcessDTO> dtoList = new ArrayList<>();
        for (LogCellectProcessCache lpc : logProcessCacheList) {
            if (!lpc.getAppId().equals(tail.getAppId())) {
                continue;
            }
            dtoList.add(LogProcessConvert.INSTANCE.cacheToTailDTO(lpc, ip, tail.getTail()));
        }
        return dtoList;
    }

    /**
     * 获取tail的日志收集进度
     *
     * @param tailId
     * @return
     */
    public List<TailLogProcessDTO> getTailLogProcess(Long tailId, String targetIp) {
        if (tailId == null) {
            return new ArrayList<>();
        }
        MilogLogTailDo logTail = logtailDao.queryById(tailId);
        List<TailLogProcessDTO> dtoList = tailProgressMap.values().stream()
                .flatMap(Collection::stream)
                .filter(collectDetail -> Objects.equals(tailId.toString(), collectDetail.getTailId()))
                .flatMap(collectDetail -> collectDetail.getFileProgressDetails().stream())
                .map(fileProgressDetail -> TailLogProcessDTO.builder()
                        .tailName(logTail.getTail())
                        .collectTime(fileProgressDetail.getCollectTime())
                        .collectPercentage(fileProgressDetail.getCollectPercentage())
                        .ip(fileProgressDetail.getConfigIp())
                        .path(fileProgressDetail.getPattern())
                        .fileRowNumber(fileProgressDetail.getFileRowNumber()).build())
                .filter(processDTO -> StringUtils.isNotBlank(processDTO.getIp()))
                .collect(Collectors.toList());
        if (StringUtils.isNotBlank(targetIp)) {
            dtoList = dtoList.stream().filter(processDTO -> Objects.equals(targetIp, processDTO.getIp())).collect(Collectors.toList());
        }
        List<TailLogProcessDTO> perOneIpProgressList = Lists.newArrayList();
        perOneIpProgressList = getTailLogProcessDTOS(dtoList, perOneIpProgressList);
        perOneIpProgressList = filterExpireTimePath(perOneIpProgressList);
        return perOneIpProgressList;
    }

    private List<TailLogProcessDTO> getTailLogProcessDTOS(List<TailLogProcessDTO> dtoList, List<TailLogProcessDTO> perOneIpProgressList) {
        if (CollectionUtils.isNotEmpty(dtoList)) {
            // 去重取时间最新的一条
            Map<String, List<TailLogProcessDTO>> collect = dtoList.stream()
                    .collect(Collectors
                            .groupingBy(processDTO ->
                                    String.format("%s-%s", processDTO.getIp(), processDTO.getPath()))
                    );
            perOneIpProgressList = collect.keySet().stream().map(s -> {
                List<TailLogProcessDTO> tailLogProcessDTOS = collect.get(s);
                return tailLogProcessDTOS.stream()
                        .sorted(Comparator.comparing(TailLogProcessDTO::getCollectTime).reversed())
                        .findFirst().get();
            }).collect(Collectors.toList());
            return perOneIpProgressList;
        }
        return Lists.newArrayList();
    }

    /**
     * 获取store的日志收集进度
     *
     * @param storeId
     * @return
     */
    public List<TailLogProcessDTO> getStoreLogProcess(Long storeId, String targetIp) {
        if (storeId == null) {
            return new ArrayList<>();
        }
        List<MilogLogTailDo> logtailList = logtailDao.getMilogLogtailByStoreId(storeId);
        List<TailLogProcessDTO> dtoList = new ArrayList<>();
        List<TailLogProcessDTO> processList;
        for (MilogLogTailDo milogLogtailDo : logtailList) {
            processList = getTailLogProcess(milogLogtailDo.getId(), targetIp);
            if (!processList.isEmpty()) {
                dtoList.addAll(processList);
            }
        }
        return dtoList;
    }

    public List<TailLogProcessDTO> filterExpireTimePath(List<TailLogProcessDTO> tailLogProcessDTOS) {
        return tailLogProcessDTOS.stream()
                .filter(tailLogProcessDTO -> Instant.now().toEpochMilli() - tailLogProcessDTO.getCollectTime() <
                        TimeUnit.MINUTES.toMillis(MAX_INTERRUPT_TIME)).collect(Collectors.toList());
    }

}
