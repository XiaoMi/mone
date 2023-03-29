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

package com.xiaomi.mone.log.manager.controller;

import com.alibaba.nacos.api.config.ConfigService;
import com.google.gson.Gson;
import com.xiaomi.data.push.context.AgentContext;
import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.mone.log.api.model.meta.AppLogMeta;
import com.xiaomi.mone.log.api.model.meta.LogCollectMeta;
import com.xiaomi.mone.log.api.model.vo.LogCmd;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.manager.common.Result;
import com.xiaomi.mone.log.manager.common.Version;
import com.xiaomi.mone.log.manager.dao.MilogAppTopicRelDao;
import com.xiaomi.mone.log.manager.dao.MilogRegionAvailableZoneDao;
import com.xiaomi.mone.log.manager.model.bo.RegionZoneBo;
import com.xiaomi.mone.log.manager.model.pojo.ConfigPushData;
import com.xiaomi.mone.log.manager.model.pojo.LogSpaceDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogAppTopicRelDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogRegionAvailableZoneDO;
import com.xiaomi.mone.log.manager.service.impl.AgentConfigServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.MilogAgentServiceImpl;
import com.xiaomi.mone.log.manager.service.nacos.MultipleNacosConfig;
import com.xiaomi.mone.log.manager.service.nacos.impl.SpaceConfigNacosProvider;
import com.xiaomi.mone.log.manager.user.MoneUtil;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;
import com.xiaomi.youpin.docean.mvc.ContextHolder;
import com.xiaomi.youpin.docean.mvc.MvcContext;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.remoting.RPCHook;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @Author goodjava@qq.com
 * @Date 2021/6/24 16:15
 */
@Slf4j
@Controller
public class TestController {

    @Value(value = "$rocketmq_milog_namesrv_addr")
    private String address;
    @Value(value = "$rocketmq_milog_ak")
    private String ak;
    @Value(value = "$rocketmq_milog_sk")
    private String sk;

    @Resource
    private NutDao dao;

    @Resource
    private SpaceConfigNacosProvider spaceConfigNacosProvider;

    @Resource
    private RpcServer rpcServer;

    @Resource
    private MilogAgentServiceImpl milogAgentService;

    @Resource
    private MilogAppTopicRelDao milogAppTopicRelDao;

    @Resource
    private Gson gson;


    @RequestMapping(path = "/version", method = "get")
    public String version() {
        return new Version().toString();
    }

    @RequestMapping(path = "/milog/exception", method = "get")
    public String testException() throws InvocationTargetException {
        throw new InvocationTargetException(new IllegalMonitorStateException("fsdfd"), "哈哈哈哈异常");
    }

    @RequestMapping(path = "/ok", method = "get")
    public String ok() {
        IntStream.range(0, 100).forEach(value -> {
            log.info("hhhahfasfsd,{}", value);
        });
        return "ok:" + dao.fetch(LogSpaceDO.class);
    }

    @RequestMapping(path = "/test/file/inode", method = "get")
    public String testFileInode(@RequestParam(value = "file") String file) throws IOException {
        log.info("file:{}", file);
        File files = new File(file);

        BasicFileAttributes bfa = Files.readAttributes(files.toPath(), BasicFileAttributes.class);
        Object o = bfa.fileKey();
        log.info("file inode:{}", o);
        return "ok:" + dao.fetch(LogSpaceDO.class);
    }

    @RequestMapping(path = "/get/nacos/ok", method = "get")
    public String testIsOk() {
        ConfigPushData configPushData = new ConfigPushData();
        configPushData.setId(1L);
        ConfigService configService = MultipleNacosConfig.getConfigService("127.0.0.1:80");
        spaceConfigNacosProvider.setConfigService(configService);
        spaceConfigNacosProvider.getConfig("2");
        return "ok";
    }

    @RequestMapping(path = "/test/push/config", method = "get")
    public String testConfigPush() {
        AppLogMeta appLogMeta = new AppLogMeta();
        RemotingCommand req = RemotingCommand.createRequestCommand(LogCmd.logReq);
        req.setBody(new Gson().toJson(appLogMeta).getBytes());
        AgentContext.ins().map.forEach((k, v) -> {
            RemotingCommand res = rpcServer.sendMessage(v, req);
            log.info("---->{}", new String(res.getBody()));
        });
        return "ok";
    }


    /**
     * 测试删除配置-通知log-agent停止收集
     */
    @RequestMapping(path = "/test/del/config/stop/col", method = "get")
    public String testDelConfigStopColl() {
        milogAgentService.publishIncrementDel(100L, 100L, null);
        return "success";
    }

    /**
     * 根据pod ip查看部署的agentIp
     */
    @RequestMapping(path = "/test/agent/ip/k8s", method = "get")
    public Result queryAgentK8sIp(@RequestParam(value = "ip") String ip) {

        return Result.success("");
    }

    /**
     * k8s中根据agentIP获取该agent下的所有配置
     */
    @RequestMapping(path = "/test/agent/ip/config", method = "get")
    public Result queryAgentConfig(@RequestParam(value = "ip") String ip) {
        AgentConfigServiceImpl agentConfigService = Ioc.ins().getBean(AgentConfigServiceImpl.class);
        LogCollectMeta logCollectMetaFromManager = agentConfigService.getLogCollectMetaFromManager(ip);
        log.info("返回的数据：{}", gson.toJson(logCollectMetaFromManager));
        return Result.success(logCollectMetaFromManager);
    }

    @SneakyThrows
    @RequestMapping(path = "/test/db", method = "get")
    public String testDb() {
        log.info("testdb");
        List<MilogAppTopicRelDO> milogAppTopicRels = milogAppTopicRelDao.queryAppTopicList(Cnd.NEW(), null);
        TimeUnit.SECONDS.sleep(20);
        return "testDb:" + milogAppTopicRels.size();
    }


    @SneakyThrows
    @RequestMapping(path = "/test/db1", method = "get")
    public String testDb1(MvcContext context) {
        log.info("testdb1:{}", context);
        MvcContext mvcContext = ContextHolder.getContext().get();
        log.info("context:{}", context);
        List<MilogAppTopicRelDO> milogAppTopicRels = milogAppTopicRelDao.queryAppTopicList(Cnd.NEW(), null);
        return "testDb1:" + milogAppTopicRels.size();
    }

    @SneakyThrows
    @RequestMapping(path = "/test/mq/consumer", method = "get")
    public String testConsumer(@RequestParam(value = "topic") String topic,
                               @RequestParam(value = "consumerGroup") String consumerGroup,
                               @RequestParam(value = "tag") String tag) {
        consumer(topic, consumerGroup, tag);
        return "success";
    }

    @RequestMapping(path = "/test/session/set", method = "get")
    public String testSession(MvcContext context) {
        context.session().setAttribute("name", "zzy");
        return "set session success";
    }

    @RequestMapping(path = "/test/session2", method = "get")
    public String session2(MvcContext context) {
        return "session2:" + context.getSession().getAttribute(MoneUtil.MONE_USER_INFO);
    }

    @RequestMapping(path = "/test/session/get", method = "get")
    public String testSessionGet(MvcContext context) {
        return context.session().getAttribute("name").toString();
    }

    @RequestMapping(path = "/test/match/*", method = "get")
    public String match(MvcContext context) {
//        String path = context.getPath();
//        log.info(ContextHolder.getContext().get().getPath());
        return "match:" + System.currentTimeMillis() + ":";
    }


    /**
     * 测试mq消费
     *
     * @param topic
     * @param consumerGroup
     * @param tag
     * @throws MQClientException
     */
    public void consumer(String topic, String consumerGroup, String tag) throws MQClientException {
        SessionCredentials credentials = new SessionCredentials(ak, sk);
        RPCHook rpcHook = new AclClientRPCHook(credentials);
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup, rpcHook, new AllocateMessageQueueAveragely());
        consumer.setNamesrvAddr(address);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.subscribe(topic, tag);

        consumer.registerMessageListener((MessageListenerOrderly) (list, consumeOrderlyContext) -> {
            list.stream().forEach(ele -> {
                byte[] body = ele.getBody();
                log.info("消息：{}", new String(body));
            });
            return ConsumeOrderlyStatus.SUCCESS;
        });
        try {
            consumer.start();
        } catch (MQClientException e) {
            log.error("订阅创建项目时的RocketMq客户端启动异常", e);
        }
        System.out.println("transaction_Consumer Started.");
    }

    @RequestMapping(path = "/test/init/insert/region/sql", method = "get")
    public String initInsertSql() {
        String jsonStr = "";
        RegionZoneBo regionZoneBO = gson.fromJson(jsonStr, RegionZoneBo.class);
        MilogRegionAvailableZoneDao regionAvailableZoneDao = Ioc.ins().getBean(MilogRegionAvailableZoneDao.class.getCanonicalName());
        regionZoneBO.getData().forEach(innerClass -> {
            if (innerClass.getIs_used()) {
                MilogRegionAvailableZoneDO milogRegionAvailableZoneDO = new MilogRegionAvailableZoneDO();
                milogRegionAvailableZoneDO.setRegionNameEN(innerClass.getRegion_en());
                milogRegionAvailableZoneDO.setRegionNameCN(innerClass.getRegion_cn());
                milogRegionAvailableZoneDO.setZoneNameCN(innerClass.getZone_name_cn());
                milogRegionAvailableZoneDO.setZoneNameEN(innerClass.getZone_name_en());
                milogRegionAvailableZoneDO.setCtime(Instant.now().toEpochMilli());
                milogRegionAvailableZoneDO.setUtime(Instant.now().toEpochMilli());
                milogRegionAvailableZoneDO.setCreator(Constant.DEFAULT_OERATOR);
                milogRegionAvailableZoneDO.setUpdater(Constant.DEFAULT_OERATOR);
                regionAvailableZoneDao.insert(milogRegionAvailableZoneDO);
            }
        });
        System.out.println(regionZoneBO);
        return "success";
    }

    @RequestMapping(path = "/test/trace/log", method = "get")
    public Result traceLog(@RequestParam(value = "traceId") String traceId) {
        return null;
    }

}
