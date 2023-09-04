package com.xiaomi.mone.app.api.model.project.group;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author gaoxihui
 * @date 2023/6/5 3:31 下午
 */
@Data
public class HeraProjectGroupAppRequest implements Serializable {

    private Integer appId;

    private String appName;

    private Integer platFormType;

}
