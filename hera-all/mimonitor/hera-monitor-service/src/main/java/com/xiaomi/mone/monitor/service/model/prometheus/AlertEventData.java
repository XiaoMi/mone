package com.xiaomi.mone.monitor.service.model.prometheus;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author gaoxihui
 * @date 2021/9/16 8:52 下午
 */
@Data
public class AlertEventData implements Serializable {
    private String id;//唯一标识一个告警事件（同一个指标同一时间只会生成一条告警），格式为 ：df1d79e46d403e503ef024340cf589f4
    private String event_id;//唯一标识一个告警（同一个指标产生的告警），格式为，treeId_alertId_md5：1_24_df1d79e46d403e503ef024340cf589f4
    private Integer alert_id;//alert id
    private Integer tree_id;//iam tree id
    private String alert_name;//alert name
    private String alert_cname;//告警规则别名
    private String expr;//告警表达式
    private String metric;//metric name
    private Map labels;//labels，包含原始数据的 labels 和 alert 里配置的 labels
    private Map annotations;//告警规则描述
    private String status;//告警状态，firing、resolved
    private Long alert_time;//当前告警时间
    private Float alert_value;//告警值
    private String env;//配置环境
    private String priority;//告警级别
    private String created_by;//告警创建人
    private Long start_time;//告警开始时间
    private Long end_time;//timestamp
    private Integer duration;//告警持续时间，秒

}
