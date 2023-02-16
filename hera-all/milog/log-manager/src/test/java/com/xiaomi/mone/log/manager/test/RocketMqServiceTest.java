package com.xiaomi.mone.log.manager.test;

import com.google.gson.Gson;
import com.xiaomi.mone.log.api.enums.ProjectSourceEnum;
import com.xiaomi.mone.log.api.model.meta.LogCollectMeta;
import com.xiaomi.mone.log.manager.service.RocketMqService;
import com.xiaomi.mone.log.manager.service.impl.AgentConfigServiceImpl;
import com.xiaomi.youpin.docean.Ioc;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

/**
 * @author wtt
 * @version 1.0
 * @description rocketMQ 操作topic 测试
 * @date 2021/6/30 14:45
 */
public class RocketMqServiceTest {

    @Test
    public void testCreateTopic() {
        Ioc.ins().init("com.xiaomi");
        RocketMqService rocketMqService = Ioc.ins().getBean(RocketMqService.class);
    }

    @Test
    public void testDeleteTopic() {
        Ioc.ins().init("com.xiaomi");
        RocketMqService rocketMqService = Ioc.ins().getBean(RocketMqService.class);
        String topicName = "milog_test";
        String serviceTopic = rocketMqService.deleteTopic(1L, 2L, "testProject", topicName);
        Assert.assertNotNull(serviceTopic);
    }

    @Test
    public void testUpdateMqConfig() {
        Ioc.ins().init("com.xiaomi");
        RocketMqService rocketMqService = Ioc.ins().getBean(RocketMqService.class);
    }

    @Test
    public void testCreateConsumerGroup() {
        Ioc.ins().init("com.xiaomi");
        RocketMqService rocketMqService = Ioc.ins().getBean(RocketMqService.class);
        boolean tagSuccess = rocketMqService.createConsumerGroup(100L, 200L, 120L, null);
        Assert.assertTrue(tagSuccess);
    }

    @Test
    public void deleteCreateConsumerGroup() {
        Ioc.ins().init("com.xiaomi");
        RocketMqService rocketMqService = Ioc.ins().getBean(RocketMqService.class);
        boolean tagSuccess = rocketMqService.deleteConsumerGroup(1L, 2L, 1L);
        Assert.assertTrue(tagSuccess);
    }

    @Test
    public void updateTopiSubGroupAuth() {
        Ioc.ins().init("com.xiaomi");
        RocketMqService rocketMqService = Ioc.ins().getBean(RocketMqService.class);
        boolean tagSuccess = rocketMqService.updateTopiSubGroupAuth("122_testProject0_202108040423");
        Assert.assertTrue(tagSuccess);
    }


    @Test
    public void test() {
        Ioc.ins().init("com.xiaomi");
        AgentConfigServiceImpl rocketMqService = Ioc.ins().getBean(AgentConfigServiceImpl.class);
        LogCollectMeta logCollectMetaFromManager = rocketMqService.getLogCollectMetaFromManager("127.0.0.1:1");
        System.out.println(new Gson().toJson(logCollectMetaFromManager));

    }

    /**
     * 测试查询mq过滤
     */
    @Test
    public void testMqQuery() {
        Ioc.ins().init("com.xiaomi");
        RocketMqService rocketMqService = Ioc.ins().getBean(RocketMqService.class);
        Set<String> queryExistTopics = rocketMqService.queryExistTopic();
        System.out.println(new Gson().toJson(queryExistTopics));

    }
}
