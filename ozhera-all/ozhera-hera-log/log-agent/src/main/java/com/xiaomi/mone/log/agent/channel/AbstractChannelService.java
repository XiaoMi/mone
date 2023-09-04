/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.agent.channel;

import com.xiaomi.mone.log.agent.channel.memory.ChannelMemory;
import com.xiaomi.mone.log.agent.input.Input;
import com.xiaomi.mone.log.api.enums.LogTypeEnum;
import com.xiaomi.mone.log.api.model.meta.LogPattern;
import com.xiaomi.mone.log.utils.NetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/6/20 16:26
 */
@Slf4j
public abstract class AbstractChannelService implements ChannelService {

    public String instanceId = UUID.randomUUID().toString();

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
        channelState.setIpList(getChannelDefine().getIpDirectoryRel().stream().map(LogPattern.IPRel::getIp).distinct().collect(Collectors.toList()));

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
            stateProgress.setCtTime(fileProcess.getCtTime());
            channelState.getStateProgressMap().put(pattern, stateProgress);
        });

        channelState.setTotalSendCnt(getLogCounts());
        return channelState;
    }

    public abstract ChannelDefine getChannelDefine();

    public abstract ChannelMemory getChannelMemory();

    public abstract Map<String, Long> getExpireFileMap();

    public abstract void cancelFile(String file);

    public abstract Long getLogCounts();

    public LogTypeEnum getLogTypeEnum() {
        Input input = getChannelDefine().getInput();
        return LogTypeEnum.name2enum(input.getType());
    }

    /**
     * Query IP information based on the actual collection path.
     *
     * @param pattern
     * @return
     */
    protected String getTailPodIp(String pattern) {
        ChannelDefine channelDefine = getChannelDefine();
        List<LogPattern.IPRel> ipDirectoryRel = channelDefine.getIpDirectoryRel();
        LogPattern.IPRel actualIpRel = ipDirectoryRel.stream()
                .filter(ipRel -> pattern.contains(ipRel.getKey()))
                .findFirst()
                .orElse(null);
        if (null != actualIpRel) {
            return actualIpRel.getIp();
        }
        return NetUtil.getLocalIp();
    }

}
