package com.xiaomi.mone.monitor.bo;

import lombok.Data;

import java.util.Date;

/**
 * @author zhangxiaowei6
 * @date 2023-02-23
 */
@Data
public class GrafanaFolderRes {
    private int id;
    private String uid;
    private String title;
    private String url;
    private boolean hasAcl;
    private boolean canSave;
    private boolean canEdit;
    private boolean canAdmin;
    private String createdBy;
    private Date created;
    private String updatedBy;
    private Date updated;
    private int version;
}