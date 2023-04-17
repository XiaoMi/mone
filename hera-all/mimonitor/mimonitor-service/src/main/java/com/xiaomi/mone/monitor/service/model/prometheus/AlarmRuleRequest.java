package com.xiaomi.mone.monitor.service.model.prometheus;

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author gaoxihui
 * @date 2021/9/14 9:10 下午
 */
@Data
@ToString
public class AlarmRuleRequest implements Serializable {

    private Integer iamId;
    private Integer projectId;
    private Integer ruleTemplateId;
    @Deprecated
    private String remark;
    private Integer ruleStatus;
    private Integer strategyId;
    private Integer strategyType;
    private String strategyName;
    private String strategyDesc;
    private String alertTeam;
    private String appAlias;
    private List<String> includeEnvs;//包含环境列表
    private List<String> exceptEnvs;//不包含环境列表
    private List<String> includeServices;//包含service列表
    private List<String> exceptServices;//不包含service列表
    private List<String> includeModules;//包含模块列表
    private List<String> exceptModules;//不包含模块列表
    private List<String> includeFunctions;//包含函数列表
    private List<String> exceptFunctions;//不包含函数列表
    private List<String> alertMembers;//报警人员列表
    private List<String> atMembers;//@人员列表
    private List<AlarmRuleData> alarmRules;
    private String user;//但前操作人

    public String convertEnvs(){

        JsonObject envs = new JsonObject();
        if(!CollectionUtils.isEmpty(this.getIncludeEnvs())){
            envs.addProperty("includeEnvs",String.join(",", this.getIncludeEnvs()));
        }
        if(!CollectionUtils.isEmpty(this.getExceptEnvs())){
            envs.addProperty("exceptEnvs",String.join(",", this.getExceptEnvs()));
        }

        if(!CollectionUtils.isEmpty(this.getIncludeServices())){
            envs.addProperty("includeServices",String.join(",", this.getIncludeServices()));
        }
        if(!CollectionUtils.isEmpty(this.getExceptEnvs())){
            envs.addProperty("exceptServices",String.join(",", this.getExceptServices()));
        }

        if(!CollectionUtils.isEmpty(this.getIncludeModules())){
            envs.addProperty("includeModules",String.join(",", this.getIncludeModules()));
        }
        if(!CollectionUtils.isEmpty(this.getExceptModules())){
            envs.addProperty("exceptModules",String.join(",", this.getExceptModules()));
        }

        if(!CollectionUtils.isEmpty(this.getIncludeFunctions())){
            envs.addProperty("includeFunctions",String.join(",", this.getIncludeFunctions()));
        }
        if(!CollectionUtils.isEmpty(this.getExceptFunctions())){
            envs.addProperty("exceptFunctions",String.join(",", this.getExceptFunctions()));
        }

        return envs.toString();
    }
}
