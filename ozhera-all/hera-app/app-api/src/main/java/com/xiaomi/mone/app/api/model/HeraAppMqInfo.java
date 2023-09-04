package com.xiaomi.mone.app.api.model;

import com.xiaomi.mone.app.enums.OperateEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/11 19:11
 */
@Data
public class HeraAppMqInfo implements Serializable {

    private OperateEnum operateEnum;

    private HeraAppBaseInfo beforeAppBaseInfo;

    private HeraAppBaseInfo afterAppBaseInfo;

    @Data
    public static class HeraAppBaseInfo {
        private Integer id;

        private String bindId;

        private Integer bindType;

        private String appName;

        private String appCname;

        private Integer appType;

        private String appLanguage;

        private Integer platformType;

        private String appSignId;

        private Integer iamTreeId;

        private Integer status;

        private Date createTime;

        private Date updateTime;

        private String envsMap;
    }
}
