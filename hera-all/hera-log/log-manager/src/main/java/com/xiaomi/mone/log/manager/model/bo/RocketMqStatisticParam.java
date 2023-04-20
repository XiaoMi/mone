package com.xiaomi.mone.log.manager.model.bo;

import lombok.Data;

import java.util.List;

@Data
public class RocketMqStatisticParam {
    private List<String> topicList;
    private List<String> groupList;
    /**
     * 参数可选值：(*或broker组名)，若未null，则返回所有broker组之和
     */
    private String broker;
    private Long begin;
    private Long end;
    /**
     * 参数可选值:(avg、sum、min、max)   默认avg
     */
    private String aggregator;

    private String client;
    private String metirc;
}
