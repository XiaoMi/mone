package com.xiaomi.mone.app.api.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author gaoxihui
 * @date 2022/11/22 7:35 下午
 */
@Data
@ToString
public class HeraAppRoleModel implements Serializable {

        private Integer id;

        private String appId;

        private Integer appPlatform;

        private String user;

        private Integer role;

        private Integer status;

        private Date createTime;

        private Date updateTime;

}
