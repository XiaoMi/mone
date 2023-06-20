package com.xiaomi.mone.log.agent.channel;

import com.xiaomi.mone.log.agent.channel.memory.ChannelMemory;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/6/20 16:26
 */
public abstract class AbstractChannelService implements ChannelService {

    public String instanceId = UUID.randomUUID().toString();

    /**
     * 实际采集的文件对应的机器Ip(兼容k8s 一个node下多个pod的问题)
     */
    public Map<String, String> ipPath = new ConcurrentHashMap<>();

    @Override
    public String instanceId() {
        return instanceId;
    }

    @Override
    public ChannelState state() {
        ChannelState channelState = new ChannelState();

        ChannelDefine channelDefine = getChannelDefine();
        ChannelMemory channelMemory = getChannelMemory();

        channelState.setTailId(channelDefine.getChannelId());
        channelState.setTailName(channelDefine.getTailName());
        channelState.setAppId(channelDefine.getAppId());
        channelState.setAppName(channelDefine.getAppName());
        channelState.setLogPattern(channelDefine.getInput().getLogPattern());
        channelState.setLogPatternCode(channelDefine.getInput().getPatternCode());
        channelState.setIpList(ipPath.values().stream().distinct().collect(Collectors.toList()));

        channelState.setCollectTime(channelMemory.getCurrentTime());

        if (channelState.getStateProgressMap() == null) {
            channelState.setStateProgressMap(new HashMap<>(256));
        }
        channelMemory.getFileProgressMap().forEach((pattern, fileProcess) -> {
            if (null != fileProcess.getFinished() && fileProcess.getFinished()) {
                return;
            }
            ChannelState.StateProgress stateProgress = new ChannelState.StateProgress();
            stateProgress.setCurrentFile(pattern);
            stateProgress.setIp(getTailPodIp(pattern));
            stateProgress.setCurrentRowNum(fileProcess.getCurrentRowNum());
            stateProgress.setPointer(fileProcess.getPointer());
            stateProgress.setFileMaxPointer(fileProcess.getFileMaxPointer());
            channelState.getStateProgressMap().put(pattern, stateProgress);
        });

        channelState.setTotalSendCnt(getLogCounts());
        return channelState;
    }

    public abstract ChannelDefine getChannelDefine();

    public abstract ChannelMemory getChannelMemory();

    public abstract Long getLogCounts();

    private String getTailPodIp(String pattern) {
        String tailPodIp = ipPath.get(pattern);
        if (StringUtils.isBlank(tailPodIp)) {
            Optional<String> ipOptional = ipPath.keySet().stream().filter(path -> pattern.startsWith(path)).findFirst();
            String ipKey = ipPath.keySet().stream().findFirst().get();
            if (ipOptional.isPresent()) {
                ipKey = ipOptional.get();
            }
            tailPodIp = ipPath.get(ipKey);
        }
        return tailPodIp;
    }
}
