package com.xiaomi.mone.monitor.service.rocketmq.model;

import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.dao.model.AppMonitor;
import com.xiaomi.mone.monitor.dao.model.HeraAppBaseInfo;
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
public class HeraAppMessage implements Serializable {

    private String id;

    private Integer iamTreeId;

    private String appName;

    private String appCname;

    private String owner;

    private Integer platformType;

    private Integer bindType;

    private String appLanguage;

    private Integer appType;

    private JsonObject envMapping;

    private List<String> joinedMembers;

    private Integer delete;

    public AppMonitor appMonitor(){
        AppMonitor appMonitor = new AppMonitor();
        BeanUtils.copyProperties(this,appMonitor);
        return appMonitor;
    }

    public HeraAppBaseInfo baseInfo(){

        HeraAppBaseInfo heraAppBaseInfo = new HeraAppBaseInfo();

        heraAppBaseInfo.setBindId(String.valueOf(this.getId()));
        heraAppBaseInfo.setBindType(this.getBindType());
        heraAppBaseInfo.setAppName(this.getAppName());
        heraAppBaseInfo.setAppCname(this.getAppCname());

        heraAppBaseInfo.setAppLanguage(this.getAppLanguage());
        heraAppBaseInfo.setPlatformType(this.getPlatformType());
        heraAppBaseInfo.setAppType(this.getAppType());
        heraAppBaseInfo.setEnvsMap(this.getEnvMapping() == null ? "" : this.getEnvMapping().toString());

        heraAppBaseInfo.setIamTreeId(this.getIamTreeId());

        return heraAppBaseInfo;
    }

}
