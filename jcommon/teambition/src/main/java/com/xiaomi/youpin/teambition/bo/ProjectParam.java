package com.xiaomi.youpin.teambition.bo;

import lombok.Data;

/**
 * @author wmin
 * @date 2021/9/22
 */
@Data
public class ProjectParam {
    private String name;
    private String userId;//用于查询用户加入了哪些项目
    private String projectId;
    private int isArchived;
    private int pageSize;
    private String pageToken;
}
