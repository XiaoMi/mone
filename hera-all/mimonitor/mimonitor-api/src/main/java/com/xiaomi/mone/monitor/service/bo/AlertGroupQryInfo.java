package com.xiaomi.mone.monitor.service.bo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author zhanggaofeng1
 */
@Data
@ToString
public class AlertGroupQryInfo implements Serializable  {

    private long id;
    private String name;
    private String note;
    private String chatId;
    private int treeId;
    private String createdBy;
    private long createdTime;
    private String type;
    private long relId;

}
