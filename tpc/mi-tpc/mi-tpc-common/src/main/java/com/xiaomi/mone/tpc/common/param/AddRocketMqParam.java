package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.RocketMqOffsetTypeEnum;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @project: mi-tpc
 * @author: zhangxiaowei6
 * @date: 2022/3/24
 */
@Data
@ToString(callSuper = true)
public class AddRocketMqParam implements ArgCheck, Serializable {
    private String rocketMQAK;
    private String rocketMQSK;
    private String rocketMQAddress;
    private String pGroup;//p
    private String name;
    private String type;
    private Integer id;
    private String cGroup;//c
    private int cThreadMin;//c
    private int cThreadMax;//c
    private int consumerWhere;//c 0,4,5
    private long consumerTime;//c 毫秒
    private boolean useProvider = true;//p
    private boolean useConsumer = true;//c
    private int sendTryTimes;//p
    private int pTimeout;
    private int cTimeout;
    private int cBatchMaxSize;
    private int cPullBatchSize;
    private int cmaxReConsumeTimes;
    private int cPullInterval;
    private List<RocketMqTopic> topics;
    private boolean cEnableMsgTrace;//是否开启消费轨迹
    private String cCustomizedTraceTopic;//消费轨迹主题
    private boolean pEnableMsgTrace;//是否开启消费轨迹
    private String pCustomizedTraceTopic;//消费轨迹主题

    @Override
    public void encrypted() {
        if (StringUtils.isNotBlank(rocketMQAK)) {
            rocketMQAK = "******";
        }
        if (StringUtils.isNotBlank(rocketMQSK)) {
            rocketMQSK = "******";
        }
    }

    @Override
    public boolean argCheck() {
        if (StringUtils.isBlank(rocketMQAK)) {
            return false;
        }
        if (StringUtils.isBlank(rocketMQSK)) {
            return false;
        }
        if (StringUtils.isBlank(rocketMQAddress)) {
            return false;
        }
        if (useProvider) {
            if (StringUtils.isBlank(pGroup)) {
                return false;
            }
        }
        if (useConsumer) {
            if (StringUtils.isBlank(cGroup)) {
                return false;
            }
            RocketMqOffsetTypeEnum typeEnum = RocketMqOffsetTypeEnum.getEnum(consumerWhere);
            if (typeEnum == null) {
                return false;
            }
            if (RocketMqOffsetTypeEnum.CONSUME_FROM_TIMESTAMP.equals(typeEnum) && consumerTime <= 0) {
                return false;
            }
            if (topics != null && !topics.isEmpty()) {
                for (RocketMqTopic topic : topics) {
                    if (StringUtils.isEmpty(topic.getTopic())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
