package com.xiaomi.mone.log.manager.model.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/27 11:22
 */
@Data
public class AppTopicParam implements Serializable {

    private Long appId;

    private String appName;

    private Integer pageSize=10;

    private Integer page=1;

//    private String type;
//
//    private String ak;
//
//    private String sk;
//
//    private String topic;
//
//    private String tag;
//
//    private Integer partitionCnt;
}
