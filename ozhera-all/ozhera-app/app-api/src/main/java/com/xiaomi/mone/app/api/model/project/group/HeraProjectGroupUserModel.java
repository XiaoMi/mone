package com.xiaomi.mone.app.api.model.project.group;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author gaoxihui
 * @date 2023/6/2 10:13 上午
 */
@Data
public class HeraProjectGroupUserModel implements Serializable {

    private Integer id;

    private Integer projectGroupId;

    private String user;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}
