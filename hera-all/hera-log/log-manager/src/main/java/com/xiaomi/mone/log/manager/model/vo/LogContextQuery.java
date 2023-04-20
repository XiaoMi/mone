package com.xiaomi.mone.log.manager.model.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class LogContextQuery implements Serializable {
    private String logstore;
    private String ip;
    private String fileName;
    private Long lineNumber;
    private String timestamp;
    private Integer pageSize;
    private Integer type; //0-around;1-after;2-before
}
