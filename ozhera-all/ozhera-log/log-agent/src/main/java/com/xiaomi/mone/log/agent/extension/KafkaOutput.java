package com.xiaomi.mone.log.agent.extension;

import com.xiaomi.mone.log.agent.output.Output;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode
public class KafkaOutput extends Output implements Serializable {

    public static final String OUTPUT_KAFKAMQ = "kafkamq";

    private String serviceName = "KafkaMQService";

    /**
     * mq fill：namesrv_addr
     */
    private String clusterInfo;

    private String producerGroup;

    private String orgId;

    private String ak;

    private String sk;

    private String topic;

    private Integer partitionCnt;

    private Integer batchExportSize;

    @Override
    public String getEndpoint() {
        return clusterInfo;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }
}
