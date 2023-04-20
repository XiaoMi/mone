package com.xiaomi.mone.monitor.bo;

import com.xiaomi.mone.monitor.enums.ProcessAlertSourceEnum;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import run.mone.health.check.common.vo.InterfaceVo;

/**
 * @project: mimonitor
 * @author: zgf1
 * @date: 2022/7/21 19:18
 */
@Data
@ToString
public class HealthCheckCfgVo {

    private java.lang.Long id;
    private java.lang.String projectId;
    private java.lang.String projectName;
    private java.lang.String projectEnvId;
    private java.lang.String projectEnvName;
    private java.lang.Integer status;
    private InterfaceVo ivo;
    private java.lang.Integer source;
    /**
     * 以下回显使用
     */
    private java.lang.String createrAcc;
    private java.lang.String updaterAcc;
    private long createTime;
    private long updateTime;

    public boolean addArgCheck() {
        if (StringUtils.isBlank(projectId)) {
            return false;
        }
        if (StringUtils.isBlank(projectName)) {
            return false;
        }
        if (StringUtils.isBlank(projectEnvId)) {
            return false;
        }
        if (StringUtils.isBlank(projectEnvName)) {
            return false;
        }
        ProcessAlertSourceEnum sourceEnum = ProcessAlertSourceEnum.getEnumByOutCode(source);
        if (sourceEnum == null) {
            return false;
        }
        if (ivo == null) {
            return false;
        }
        if (status == null) {
            status = 0;
        }
        return true;
    }

    public boolean editArgCheck() {
        if (id == null) {
            return false;
        }
        return true;
    }


}