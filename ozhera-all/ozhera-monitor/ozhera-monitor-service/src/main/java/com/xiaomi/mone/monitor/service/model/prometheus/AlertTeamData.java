package com.xiaomi.mone.monitor.service.model.prometheus;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gaoxihui
 * @date 2021/9/16 8:52 下午
 */
@Data
public class AlertTeamData implements Serializable {
    private Integer id;//oncall 组 id
    String name;//oncall 组名称
    String note;//oncall 组件简介
    String[] duty_users;//当前 oncall 人员
    String manager;//值班经理
    String[] members;//成员
    String chat_id;//飞书群 ID
    String cretaed_by;//创建人
    Long created_time;//创建时间
    Integer goc_oncall_id;//migoc oncall id

}
