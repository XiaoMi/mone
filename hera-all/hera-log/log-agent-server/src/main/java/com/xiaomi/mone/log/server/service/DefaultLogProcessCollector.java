package com.xiaomi.mone.log.server.service;

import cn.hutool.core.util.NumberUtil;
import com.google.common.collect.Lists;
import com.xiaomi.mone.log.api.model.vo.AgentLogProcessDTO;
import com.xiaomi.mone.log.api.model.vo.TailLogProcessDTO;
import com.xiaomi.mone.log.api.model.vo.UpdateLogProcessCmd;
import com.xiaomi.mone.log.api.service.LogProcessCollector;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/12/6 14:32
 */
@Slf4j
@Component
@Service(interfaceClass = LogProcessCollector.class, group = "$dubbo.group", timeout = 10000)
public class DefaultLogProcessCollector implements LogProcessCollector {

    private final Map<String, List<UpdateLogProcessCmd.CollectDetail>> tailProgressMap = new ConcurrentHashMap<>(256);

    private static final Integer MAX_INTERRUPT_TIME = 10;

    private static final Integer MAX_STATIC_INTERRUPT_TIME_HOUR = 4;

    private static final String PROCESS_SEPARATOR = "%";

    @Override
    public void collectLogProcess(UpdateLogProcessCmd cmd) {
        log.debug("[LogProcess.updateLogProcess] cmd:{} ", cmd);
        if (cmd == null || StringUtils.isEmpty(cmd.getIp())) {
            return;
        }
        tailProgressMap.put(cmd.getIp(), cmd.getCollectList());
    }

    @Override
    public List<TailLogProcessDTO> getTailLogProcess(Long tailId, String tailName, String targetIp) {
        if (null == tailId || StringUtils.isBlank(tailName)) {
            return new ArrayList<>();
        }
        List<TailLogProcessDTO> dtoList = tailProgressMap.values().stream()
                .flatMap(Collection::stream)
                .filter(collectDetail -> Objects.equals(tailId.toString(), collectDetail.getTailId()))
                .flatMap(collectDetail -> collectDetail.getFileProgressDetails().stream())
                .map(fileProgressDetail -> TailLogProcessDTO.builder()
                        .tailName(tailName)
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

    @Override
    public List<AgentLogProcessDTO> getAgentLogProcess(String ip) {
        List<AgentLogProcessDTO> dtoList = Lists.newArrayList();
        if (StringUtils.isEmpty(ip) || tailProgressMap.isEmpty()) {
            return dtoList;
        }
        List<UpdateLogProcessCmd.CollectDetail> collect = tailProgressMap.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)) {
            return dtoList;
        }
        collect.stream().forEach(collectDetail -> {
            String appName = collectDetail.getAppName();
            dtoList.addAll(collectDetail.getFileProgressDetails().stream()
                    .filter(processDTO -> com.xiaomi.youpin.docean.common.StringUtils.isNotBlank(processDTO.getConfigIp()))
                    .filter(processDTO -> Objects.equals(ip, processDTO.getConfigIp()))
                    .map(fileProgressDetail -> {
                        AgentLogProcessDTO agentLogProcessDTO = new AgentLogProcessDTO();
                        agentLogProcessDTO.setPath(fileProgressDetail.getPattern());
                        agentLogProcessDTO.setFileRowNumber(fileProgressDetail.getFileRowNumber());
                        agentLogProcessDTO.setPointer(fileProgressDetail.getPointer());
                        agentLogProcessDTO.setFileMaxPointer(fileProgressDetail.getFileMaxPointer());
                        agentLogProcessDTO.setAppName(appName);
                        agentLogProcessDTO.setCollectPercentage(fileProgressDetail.getCollectPercentage());
                        agentLogProcessDTO.setCollectTime(fileProgressDetail.getCollectTime());
                        return agentLogProcessDTO;
                    }).collect(Collectors.toList()));
        });
        return dtoList;
    }

    @Override
    public List<UpdateLogProcessCmd.CollectDetail> getColProcessImperfect(Double progressRation) {
        List<UpdateLogProcessCmd.CollectDetail> resultList = Lists.newArrayList();
        if (null == progressRation || tailProgressMap.isEmpty()) {
            return resultList;
        }
        resultList = tailProgressMap.values().stream().flatMap(Collection::stream)
                .map(collectDetail -> {
                    List<UpdateLogProcessCmd.FileProgressDetail> fileProgressDetails = collectDetail.getFileProgressDetails();
                    if (CollectionUtils.isNotEmpty(fileProgressDetails)) {
                        List<UpdateLogProcessCmd.FileProgressDetail> progressDetails = fileProgressDetails.stream()
                                .filter(fileProgressDetail -> lessThenRation(fileProgressDetail.getCollectPercentage(), progressRation))
                                .filter(tailLogProcessDTO -> Instant.now().toEpochMilli() - tailLogProcessDTO.getCollectTime() <
                                        TimeUnit.HOURS.toMillis(MAX_STATIC_INTERRUPT_TIME_HOUR))
                                .collect(Collectors.toList());
                        collectDetail.setFileProgressDetails(progressDetails);
                    }
                    return collectDetail;
                })
                .filter(collectDetail -> CollectionUtils.isNotEmpty(collectDetail.getFileProgressDetails()))
                .collect(Collectors.toList());
        return resultList;
    }

    @Override
    public List<UpdateLogProcessCmd.FileProgressDetail> getFileProcessDetailByTail(Long tailId) {
        List<UpdateLogProcessCmd.FileProgressDetail> resultList = new ArrayList<>();
        if (tailId == null) {
            return resultList;
        }
        try {
            for (List<UpdateLogProcessCmd.CollectDetail> details : tailProgressMap.values()) {
                for (UpdateLogProcessCmd.CollectDetail detail : details) {
                    if (String.valueOf(tailId).equals(detail.getTailId())) {
                        resultList.addAll(detail.getFileProgressDetails());
                    }
                }
            }
        } catch (Throwable t) {
            log.error("getFileProcessDetailByTail error : ", t);
        }
        return resultList;
    }

    /**
     * @param source    89%
     * @param targetNum 0.98
     * @return
     */
    private boolean lessThenRation(String source, Double targetNum) {
        try {
            double sourceOrigin = Double.parseDouble(StringUtils.substringBefore(source, PROCESS_SEPARATOR));
            double sourceNum = NumberUtil.div(sourceOrigin, 100d);
            return Double.valueOf(sourceNum).compareTo(targetNum) < 0;
        } catch (Exception e) {
            log.error("lessThenRation error,source:{},target:{}", source, targetNum, e);
        }
        return true;
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

    private List<TailLogProcessDTO> filterExpireTimePath(List<TailLogProcessDTO> tailLogProcessDTOS) {
        return tailLogProcessDTOS.stream()
                .filter(tailLogProcessDTO -> Instant.now().toEpochMilli() - tailLogProcessDTO.getCollectTime() <
                        TimeUnit.MINUTES.toMillis(MAX_INTERRUPT_TIME)).collect(Collectors.toList());
    }
}
