package com.xiaomi.mone.log.stream.plugin.mq.talos;

import com.xiaomi.mone.log.stream.job.SinkJobEnum;
import com.xiaomi.mone.log.stream.plugin.mq.MQPlugin;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.xiaomi.mone.log.common.Constant.COMMON_MQ_PREFIX;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/28 16:53
 */
@Slf4j
@Service
public class TalosMqPlugin implements MQPlugin {

    public static Map<String, TalosConfig> talosConfigMap = new HashMap<>();

    public static TalosConfig buildTalosConsumer(String ak, String sk, String clusterInfo, String topicName,
                                                 String tag, SinkJobEnum jobType) {
        TalosConfig config = new TalosConfig();
        String uniqueKey = String.format("%s_%s_%s", ak, clusterInfo, topicName);
        String consumerGroup = "logSystemGroup" + topicName;
        if (topicName.startsWith(COMMON_MQ_PREFIX) && StringUtils.isNotBlank(tag)) {
            uniqueKey = String.format("%s_%s", uniqueKey, tag);
            config.setCommonTag(Boolean.TRUE);
            consumerGroup = String.format("%s_%s", consumerGroup, tag);
        }
        if (SinkJobEnum.BACKUP_JOB == jobType) {
            consumerGroup = String.format("%s_%s", consumerGroup, BACKUP_PREFIX);
            uniqueKey = String.format("%s_%s", uniqueKey, BACKUP_PREFIX);
        }
        if (talosConfigMap.containsKey(uniqueKey)) {
            return talosConfigMap.get(uniqueKey);
        }

        config.setConsumerGroup(consumerGroup);
        config.setClientPrefix("logSystem-");
        Properties pros = new Properties();
        pros.setProperty("galaxy.talos.service.endpoint", clusterInfo);
        pros.setProperty("galaxy.talos.client.falcon.monitor.switch", "false");
        pros.setProperty("galaxy.talos.consumer.max.fetch.records", "2000");
        pros.setProperty("galaxy.talos.consumer.fetch.interval.ms", "50");
        if (SinkJobEnum.BACKUP_JOB == jobType) {
            pros.setProperty("galaxy.talos.consumer.max.fetch.records", "1500");
            pros.setProperty("galaxy.talos.consumer.fetch.interval.ms", "150");
            // 指定从最新位置开始消费，只用初始化一次,不要轻易打开，不然会丢数据
//            pros.setProperty("galaxy.talos.consumer.start.whether.reset.offset", "true");
//            pros.setProperty("galaxy.talos.consumer.start.reset.offset.value", "-2");
        }
//        TalosConsumerConfig consumerConfig = new TalosConsumerConfig(pros);
//        config.setConsumerConfig(consumerConfig);
//        Credential credential = new Credential();
//        credential.setSecretKeyId(ak)
//                .setSecretKey(sk)
//                .setType(UserType.DEV_XIAOMI);
//        config.setCredential(credential);
//        config.setTopicName(topicName);
//        config.setTag(tag);
//        config.setAk(ak);
//        config.setSk(sk);
//        config.setClusterInfo(clusterInfo);
//        talosConfigMap.put(uniqueKey, config);
        return config;
    }
}
