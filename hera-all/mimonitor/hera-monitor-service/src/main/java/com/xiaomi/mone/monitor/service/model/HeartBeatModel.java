package com.xiaomi.mone.monitor.service.model;

import com.google.common.collect.Lists;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import lombok.Data;
import lombok.ToString;
import run.mone.health.check.common.enums.AppSourceEnum;
import run.mone.health.check.common.vo.ContentVo;
import run.mone.health.check.common.vo.HealthCheckInfoVo;
import run.mone.health.check.common.vo.InterfaceVo;

import java.io.Serializable;
import java.util.Date;

/**
 * @author gaoxihui
 * @date 2022/7/13 3:18 下午
 */
@Data
@ToString
public class HeartBeatModel implements Serializable {

    private Long id;
    private Integer status;
    private String projectId;
    private String projectName;
    private String projectEnvId;
    private String projectEnvName;

    private Integer source;//来源（k8s/docker部署）

    /**
     * 心跳检测类型
     * @see run.mone.health.check.common.enums.InterfaceTypeEnum
     */
    private Integer type;

    private String service;//dubbo心跳类型的serviceName
    private String method;//dubbo心跳类型的method
    private String group;//dubbo心跳类型的group
    private String version;//dubbo心跳类型的version
    private String path;//http心跳类型的访问路径
    private Integer port;
    private String alertName;//选择的报警组（名称）
    private String alertLevel;//选择的报警级别（P0\P1\P2）

    public HealthCheckInfoVo convertVo(AuthUserVo userInfo){
        HealthCheckInfoVo vo = new HealthCheckInfoVo();
        vo.setId(this.getId());
        vo.setStatus(this.getStatus());
        vo.setProjectId(this.getProjectId());
        vo.setProjectName(this.getProjectName());
        vo.setProjectEnvId(this.getProjectEnvId());
        vo.setProjectEnvName(this.getProjectEnvName());
        Integer source = this.getSource() == null ? null
                : this.getSource().intValue() == 1 ? AppSourceEnum.MILINE_DOCKER.getCode()
                : this.getSource().intValue() == 3 ? AppSourceEnum.MILINE_K8S.getCode() : null;
        vo.setSource(source);
        vo.setCreaterAcc(userInfo.genFullAccount());
        vo.setUpdaterAcc(userInfo.genFullAccount());

        InterfaceVo interfaceVo = new InterfaceVo();
        interfaceVo.setAlertName(this.getAlertName());
        interfaceVo.setService(this.getService());
        interfaceVo.setMethod(this.getMethod());
        interfaceVo.setGroup(this.getGroup());
        interfaceVo.setVersion(this.getVersion());
        interfaceVo.setPath(this.getPath());
        interfaceVo.setPort(this.getPort());
        interfaceVo.setType(this.getType());
        interfaceVo.setAlertLevel(this.getAlertLevel());

        ContentVo contentVo = new ContentVo();
        contentVo.setIvos(Lists.newArrayList(interfaceVo));
        vo.setContent(contentVo);

        return vo;

    }
}
