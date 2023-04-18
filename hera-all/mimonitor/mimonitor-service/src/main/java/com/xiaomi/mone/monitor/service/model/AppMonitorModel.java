package com.xiaomi.mone.monitor.service.model;

import com.xiaomi.mone.app.api.model.HeraAppBaseInfoModel;
import com.xiaomi.mone.monitor.dao.model.AppMonitor;
import lombok.Data;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author gaoxihui
 * @date 2022/2/21 2:38 下午
 */

@ToString
@Data
public class AppMonitorModel implements Serializable {

    private Integer projectId;

    private Integer iamTreeId;

    private String projectName;

    private String projectCName;

    private String owner;

    private Integer appSource;

    private Integer bindType;

    private String appLanguage;

    private Integer appType;

    private String envMapping;

    private List<String> joinedMembers;

    public AppMonitor appMonitor(){
        AppMonitor appMonitor = new AppMonitor();
        BeanUtils.copyProperties(this,appMonitor);
        return appMonitor;
    }

    public HeraAppBaseInfoModel baseInfo(){

        HeraAppBaseInfoModel heraAppBaseInfo = new HeraAppBaseInfoModel();

        heraAppBaseInfo.setBindId(String.valueOf(this.getProjectId()));
        heraAppBaseInfo.setBindType(this.getBindType());
        heraAppBaseInfo.setAppName(this.getProjectName());
        heraAppBaseInfo.setAppCname(this.getProjectCName());

        heraAppBaseInfo.setAppLanguage(this.getAppLanguage());
        heraAppBaseInfo.setPlatformType(this.getAppSource());
        heraAppBaseInfo.setAppType(this.getAppType());
        heraAppBaseInfo.setEnvsMap(this.getEnvMapping());

        heraAppBaseInfo.setIamTreeId(this.getIamTreeId());

        return heraAppBaseInfo;
    }

}
