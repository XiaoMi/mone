package com.xiaomi.mone.monitor.service.model.prometheus;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author gaoxihui
 * @date 2021/9/14 9:12 下午
 */
@Data
public class AlarmRuleData implements Serializable {

    private Integer id;

    private Integer strategyId;

    private Integer projectId;

    private String alert;

    private String cname;

    private Integer metricType;

    private String forTime;

    private String annotations;

    private String ruleGroup;

    private String priority;

    private String env;

    private String op;

    private Float value;

    private Integer dataCount;

    private String sendInterval;

    private Integer iamId;

    private Integer templateId;

    private Integer ruleType;

    private Integer ruleStatus;

    private String remark;

    private String creater;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    private String expr;

    private String labels;

    private String alertTeam;

    private String teslaGroup;

    private String teslaUrls;

    private String excludeTeslaUrls;

    private String includeMethods;

    private String exceptMethods;

    private String includeErrorCodes;

    private String exceptErrorCodes;

    private String includeDubboServices;

    private String exceptDubboServices;

    private String alarmDetailUrl;

    private List<String> includeEnvs;//包含环境列表

    private List<String> exceptEnvs;//不包含环境列表

    private List<String> includeServices;//包含service列表

    private List<String> exceptServices;//不包含service列表

    private List<String> includeModules;//包含模块列表

    private List<String> exceptModules;//不包含模块列表

    private List<String> includeFunctions;//包含函数列表

    private List<String> exceptFunctions;//不包含函数列表

    private List<String> alertMembers;//报警人员列表

    private List<String> atMembers;

    private String alarmCallbackUrl;

    List<AlarmAlertTeamData> alertTeams;

    public void convertStrategyLables(String json){

            if(StringUtils.isBlank(json)){
                return;
            }

            JsonObject jsonEnv = new Gson().fromJson(json,JsonObject.class);
            if(jsonEnv.has("includeEnvs")){
                String includeEnvsStr = jsonEnv.get("includeEnvs").getAsString();
                this.setIncludeEnvs(Arrays.asList(includeEnvsStr.split(",")));
            }
            if(jsonEnv.has("exceptEnvs")){
                String exceptEnvsStr = jsonEnv.get("exceptEnvs").getAsString();
                this.setExceptEnvs(Arrays.asList(exceptEnvsStr.split(",")));
            }
            if(jsonEnv.has("includeServices")){
                String includeServices = jsonEnv.get("includeServices").getAsString();
                this.setIncludeServices(Arrays.asList(includeServices.split(",")));
            }
            if(jsonEnv.has("exceptServices")){
                String exceptServices = jsonEnv.get("exceptServices").getAsString();
                this.setExceptServices(Arrays.asList(exceptServices.split(",")));
            }

            if(jsonEnv.has("includeModules")){
                String includeModules = jsonEnv.get("includeModules").getAsString();
                this.setIncludeModules(Arrays.asList(includeModules.split(",")));
            }

            if(jsonEnv.has("exceptModules")){
                String exceptModules = jsonEnv.get("exceptModules").getAsString();
                this.setExceptModules(Arrays.asList(exceptModules.split(",")));
            }

            if(jsonEnv.has("includeFunctions")){
                String includeFunctions = jsonEnv.get("includeFunctions").getAsString();
                this.setIncludeFunctions(Arrays.asList(includeFunctions.split(",")));
            }

            if(jsonEnv.has("exceptFunctions")){
                String exceptFunctions = jsonEnv.get("exceptFunctions").getAsString();
                this.setExceptFunctions(Arrays.asList(exceptFunctions.split(",")));
            }

    }

    public void convertLabels(){

        if(StringUtils.isBlank(labels)){
            return;
        }

        JsonObject json = new Gson().fromJson(labels,JsonObject.class);

        this.setIncludeMethods(json.has("includeMethods") ? json.get("includeMethods").getAsString() : null);
        this.setExceptMethods(json.has("exceptMethods") ? json.get("exceptMethods").getAsString() : null);

        this.setIncludeErrorCodes(json.has("includeErrorCodes") ? json.get("includeErrorCodes").getAsString() : null);
        this.setExceptErrorCodes(json.has("exceptErrorCodes") ? json.get("exceptErrorCodes").getAsString() : null);

        this.setIncludeDubboServices(json.has("includeDubboServices") ? json.get("includeDubboServices").getAsString() : null);
        this.setExceptDubboServices(json.has("exceptDubboServices") ? json.get("exceptDubboServices").getAsString() : null);

        this.setTeslaGroup(json.has("teslaGroup") ? json.get("teslaGroup").getAsString() : null);

        this.setAlarmDetailUrl(json.has("alarmDetailUrl") ? json.get("alarmDetailUrl").getAsString() : null);

        this.setTeslaUrls(json.has("teslaUrls") ? json.get("teslaUrls").getAsString() : null);

        this.setExcludeTeslaUrls(json.has("excludeTeslaUrls") ? json.get("excludeTeslaUrls").getAsString() : null);

        this.setAlarmCallbackUrl(json.has("alarmCallbackUrl") ? json.get("alarmCallbackUrl").getAsString() : null);
    }


}
