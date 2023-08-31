package com.xiaomi.mone.monitor.service.model.prometheus;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author gaoxihui
 * @date 2021/9/17 2:25 下午
 */
@Data
public class AlarmRuleDataRemote implements Serializable {


    private Integer id;//int	alert id
    private String cname;//	alert 别名
    private String alert;//	alert name
    private String  expr;//	表达式
    private Map labels;//	map	labels
    private String annotations	;//	告警描述信息
    private String group	;//	group name
    private String env	;//	配置环境
    Boolean enabled;// bool	是否启用
    private String priority	;//	告警级别
    List<AlertTeamData> alert_team;//	map	告警组
    private String created_by	;//	创建人
    Long created_time;//	timestamp	创建时间

}
