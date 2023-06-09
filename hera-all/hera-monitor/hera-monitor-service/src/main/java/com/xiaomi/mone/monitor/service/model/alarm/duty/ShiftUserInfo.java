package com.xiaomi.mone.monitor.service.model.alarm.duty;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gaoxihui
 * @date 2023/6/9 2:56 下午
 */
@Data
public class ShiftUserInfo implements Serializable {

    private String user;//值班人邮箱前缀
    private String display_name;//用户别名（中文名）
    private Integer start_time;//值班开始时间
    private Integer end_time;//值班结束时间
    private Integer acquire_time;//认领时间，十位长度的时间戳。为0则表示未认领
    private Integer acquire_status;//认领状态，0=未认领，1=认领
    private Boolean replace_duty;//是否是代认领，是为true，否为false或为空
    private UserInfo should_oncall_user;//该时间段的原始值班人

}
