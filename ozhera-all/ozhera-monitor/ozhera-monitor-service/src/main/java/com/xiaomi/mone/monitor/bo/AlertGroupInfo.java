package com.xiaomi.mone.monitor.bo;

import com.xiaomi.mone.monitor.service.model.alarm.duty.DutyInfo;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 *
 * @author zhanggaofeng1
 */
@Data
@ToString
public class AlertGroupInfo {

    private boolean delete;
    private boolean edit;
    private long id;
    private String name;
    private String note;
    private String chatId;
    private List<UserInfo> members;
    private int treeId;
    private String createdBy;
    private long createdTime;
    private String type;
    private long relId;
    private DutyInfo dutyInfo;

}
