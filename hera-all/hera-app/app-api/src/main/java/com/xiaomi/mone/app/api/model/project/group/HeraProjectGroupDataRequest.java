package com.xiaomi.mone.app.api.model.project.group;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author gaoxihui
 * @date 2023/6/5 3:31 下午
 */
@Data
public class HeraProjectGroupDataRequest implements Serializable {

    private Integer id;

    private Integer type;

    private Integer relationObjectId;

    private String name;

    private String cnName;

    private Integer parentGroupId;

    private Integer level;

    private List<HeraProjectGroupAppRequest> apps;

    private List<String> users;


}
