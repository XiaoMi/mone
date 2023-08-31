package com.xiaomi.mone.app.api.model.project.group;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author gaoxihui
 * @date 2023/6/2 10:13 上午
 */
@Data
public class HeraProjectGroupModel implements Serializable {

    private Integer id;

    private Integer type;

    private Integer relationObjectId;

    private String name;

    private String cnName;

    private Integer parentGroupId;

    private Integer status;

    private Integer level;

    private Date createTime;

    private Date updateTime;
}
