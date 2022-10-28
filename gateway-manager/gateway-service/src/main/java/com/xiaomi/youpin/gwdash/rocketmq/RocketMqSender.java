//package com.xiaomi.youpin.gwdash.rocketmq;
//
//import com.alibaba.fastjson.JSON;
//import com.xiaomi.youpin.gwdash.bo.DockerScaleBo;
//import com.xiaomi.youpin.gwdash.bo.MachineBo;
//import com.xiaomi.youpin.gwdash.service.MilogProviderService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.rocketmq.client.producer.DefaultMQProducer;
//import org.apache.rocketmq.common.message.Message;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
///**
// * @author wtt
// * @version 1.0
// * @description
// * @date 2021/9/1 11:23
// */
//@Service
//@Slf4j
//public class RocketMqSender {
//
//    @Autowired
//    @Qualifier("gwdashEventMQProducer")
//    private DefaultMQProducer producer;
//
//    @Autowired
//    private MilogProviderService milogProviderService;
//
//    @Value("${rocket.topic.gwdash}")
//    private String rocketTopicGwdash;
//
//    @Value("${rocket.tag.docker.scale}")
//    private String rocketTagDockerScale;
//
//    @Value("${mione.env}")
//    private String mioneEnv;
//
//    /**
//     * 休眠一段时间执行发送mq消息
//     *
//     * @param projectId
//     * @param envId
//     * @param time
//     */
//    public void sendDockerScaleMqDelaySecondAsy(long projectId, long envId, Long time) {
//        CompletableFuture.runAsync(() -> {
//            try {
//                TimeUnit.SECONDS.sleep(time);
//            } catch (InterruptedException e) {
//                log.info(e.getMessage());
//            }
//            sendDockerScaleMq(projectId, envId);
//        });
//    }
//
//    /**
//     * 发送扩缩容信息mq
//     *
//     * @param projectId
//     * @param envId
//     */
//    public void sendDockerScaleMq(long projectId, long envId) {
//        DockerScaleBo.DockerScaleBoBuilder dockerScaleBoBuilder = DockerScaleBo.builder();
//        dockerScaleBoBuilder.projectId(projectId).envId(envId).mioneEnv(mioneEnv);
//        List<MachineBo> machineBos = milogProviderService.queryIpsByAppId(projectId, envId, "");
//        if (CollectionUtils.isNotEmpty(machineBos)) {
//            dockerScaleBoBuilder.ips(machineBos.stream().map(MachineBo::getIp).collect(Collectors.toList()));
//        }
//        Message msg = new Message(rocketTopicGwdash, rocketTagDockerScale, JSON.toJSONString(dockerScaleBoBuilder.build()).getBytes());
//        try {
//            producer.send(msg);
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("send gwdash sendDockerScaleMq msg error: " + e.getMessage(), e);
//        }
//    }
//}
