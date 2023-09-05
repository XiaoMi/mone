package com.xiaomi.mone.monitor.service.model.alarm.duty;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author gaoxihui
 * @date 2023/6/8 2:16 下午
 */
@Data
public class DutyInfo implements Serializable {

    private String manager;
    private Integer model_type;//标识值班模式：0=多人值班，1=主备

    /**
     * 当有值班表时，用来标记是否值班表的发送渠道是否只发送至群，0=否，1=是。
     * 当关闭仅发送至群（=0）时，给群和值班人发送飞书通知
     * 当仅发送至群（=1）时，P0不给值班人打电话，发群失败会给值班人再发一次飞书/兜底短信
     */
    private Integer chat_only;
    private List<DutyGroup> child_groups;

}
