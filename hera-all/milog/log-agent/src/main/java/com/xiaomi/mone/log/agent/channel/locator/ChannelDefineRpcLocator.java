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

package com.xiaomi.mone.log.agent.channel.locator;

import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Lists;
import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.mone.log.agent.channel.ChannelDefine;
import com.xiaomi.mone.log.agent.channel.conf.AgentTailConf;
import com.xiaomi.mone.log.agent.export.Output;
import com.xiaomi.mone.log.agent.export.RmqOutput;
import com.xiaomi.mone.log.agent.export.TalosOutput;
import com.xiaomi.mone.log.agent.filter.FilterTrans;
import com.xiaomi.mone.log.agent.input.AppLogInput;
import com.xiaomi.mone.log.api.enums.LogTypeEnum;
import com.xiaomi.mone.log.api.enums.MiddlewareEnum;
import com.xiaomi.mone.log.api.model.meta.*;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.utils.NetUtil;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.*;

/**
 * rpc方式从log-manager获取channel元数据
 *
 * @author shanwb
 * @date 2021-07-20
 */
@Slf4j
@Component
public class ChannelDefineRpcLocator implements ChannelDefineLocator {

    @Override
    public List<ChannelDefine> getChannelDefine() {
        String localIp = NetUtil.getLocalIp();
        String heraK8sEnv = NetUtil.getHeraK8sEnv();
        if (StringUtils.isNotEmpty(heraK8sEnv) && Constant.YES.toString().equals(heraK8sEnv)) {
            localIp = String.format("%s%s%s", localIp, SYMBOL_COLON, heraK8sEnv);
        }
        return getChannelDefine(localIp);
    }

    @Override
    public List<ChannelDefine> getChannelDefine(String ip) {
        try {
            LogCollectMeta meta;
            int count = 0;
            while (true) {
                meta = getLogCollectMeta(ip);
                if (null == meta || CollectionUtils.isEmpty(meta.getAppLogMetaList())) {
                    //重试10次，每次停止1s
                    TimeUnit.SECONDS.sleep(1);
                    count++;
                } else {
                    break;
                }
                if (count > 6) {
                    break;
                }
            }
            return agentTail2ChannelDefine(logCollectMeta2ChannelDefines(meta));
        } catch (Throwable ex) {
            log.error(String.format("【agent pull metadata config error】ip:%s", ip), ex);
        }
        return null;
    }

    private LogCollectMeta getLogCollectMeta(String ip) {
        RpcClient rpcClient = Ioc.ins().getBean(RpcClient.class);
        RemotingCommand res = rpcClient.sendMessage(rpcClient.getServerAddrs(), Constant.RPCCMD_AGENT_CONFIG_CODE, ip, 30000);
        String str = new String(res.getBody());
        LogCollectMeta meta = GSON.fromJson(str, LogCollectMeta.class);
        log.info("agent getChannelDefine finish:{}", str);
        return meta;
    }

    public static AgentTailConf logCollectMeta2ChannelDefines(LogCollectMeta logCollectMeta) {
        AgentTailConf agentTailConf = new AgentTailConf();
        List<ChannelDefine> channelDefines = Lists.newArrayList();
        logCollectMeta.getAppLogMetaList().forEach(appLogMeta -> {
            ChannelDefine channelDefine = new ChannelDefine();
            channelDefine.setAppId(appLogMeta.getAppId());
            channelDefine.setAppName(appLogMeta.getAppName());
            channelDefine.setPodNames(logCollectMeta.getPodNames());
            channelDefine.setSingleMetaData(logCollectMeta.getSingleMetaData());

            List<LogPattern> logPatternList = appLogMeta.getLogPatternList();
            for (LogPattern logPattern : logPatternList) {
                ChannelDefine cloneChannelDefine = ObjectUtil.clone(channelDefine);
                cloneChannelDefine.setChannelId(logPattern.getLogtailId());
                cloneChannelDefine.setTailName(logPattern.getTailName());
                //input
                AppLogInput input = new AppLogInput();
                input.setType(logPattern.getLogType() != null ? LogTypeEnum.type2enum(logPattern.getLogType()).name() : "");
                input.setLogPattern(logPattern.getLogPattern());
                input.setPatternCode(logPattern.getPatternCode());
                input.setLogSplitExpress(logPattern.getLogSplitExpress());

                //output
                Output output = null;
                if (null != logPattern.getMQConfig()) {
                    String typeName = logPattern.getMQConfig().getType();
                    MiddlewareEnum middlewareEnum = MiddlewareEnum.queryByName(typeName);
                    if (null != middlewareEnum) {
                        switch (middlewareEnum) {
                            case ROCKETMQ:
                                output = config2rmqOutput(logPattern.getMQConfig(), logPattern);
                                break;
                            default:
                                output = config2TalosOutput(logPattern.getMQConfig());
                                break;
                        }
                    }
                }
                // filter
                List<FilterDefine> filterDefines = logPattern.getFilters();
                List<FilterConf> filters = Lists.newArrayList();
                if (filterDefines != null) {
                    filters = filterDefines.stream().map(FilterTrans::filterConfTrans).collect(Collectors.toList());
                }
                cloneChannelDefine.setIps(logPattern.getIps());
                cloneChannelDefine.setFilters(filters);
                cloneChannelDefine.setInput(input);
                cloneChannelDefine.setOutput(output);
                cloneChannelDefine.setOperateEnum(logPattern.getOperateEnum());
                channelDefines.add(cloneChannelDefine);
            }
        });

        agentTailConf.setChannelDefine(channelDefines);
        agentTailConf.setAgentDefine(logCollectMeta.getAgentDefine());
        log.info("agent build metadata config:{}", GSON.toJson(agentTailConf));
        return agentTailConf;
    }

    private static RmqOutput config2rmqOutput(MQConfig mqConfig, LogPattern logPattern) {
        RmqOutput output = new RmqOutput();
        output.setOutputType(Output.OUTPUT_ROCKETMQ);
        output.setClusterInfo(mqConfig.getClusterInfo());
        output.setProducerGroup(mqConfig.getProducerGroup());
        output.setAk(mqConfig.getAk());
        output.setSk(mqConfig.getSk());
        output.setTopic(mqConfig.getTopic());
        output.setPartitionCnt(mqConfig.getPartitionCnt());
        output.setTag(mqConfig.getTag());
        output.setProducerGroup(DEFAULT_CONSUMER_GROUP + (null == logPattern.getPatternCode() ? "" : logPattern.getPatternCode()));
        return output;
    }

    private static TalosOutput config2TalosOutput(MQConfig mqConfig) {
        TalosOutput output = new TalosOutput();
        output.setOutputType(Output.OUTPUT_TALOS);
        output.setClusterInfo(mqConfig.getClusterInfo());
        output.setProducerGroup(mqConfig.getProducerGroup());
        output.setAk(mqConfig.getAk());
        output.setSk(mqConfig.getSk());
        output.setTopic(mqConfig.getTopic());
        output.setTag(mqConfig.getTag());
        output.setBatchExportSize(mqConfig.getBatchSendSize());
        return output;
    }

    /**
     * 将global filter 加入到channelDefine的filterlist中
     *
     * @param conf
     * @return
     */
    public static List<ChannelDefine> agentTail2ChannelDefine(AgentTailConf conf) {
        if (conf != null && conf.getAgentDefine() != null && CollectionUtils.isNotEmpty(conf.getAgentDefine().getFilters())) {
            List<FilterConf> filters = conf.getAgentDefine().getFilters()
                    .stream()
                    .filter(f -> f != null && f.getType().equals(FilterType.GLOBAL))
                    .collect(Collectors.toList());
            if (conf.getChannelDefine() != null && CollectionUtils.isNotEmpty(filters)) {
                conf.getChannelDefine().forEach(c -> {
                    c.getFilters().addAll(filters);
                });
            }
        }
        return conf.getChannelDefine();
    }
}
