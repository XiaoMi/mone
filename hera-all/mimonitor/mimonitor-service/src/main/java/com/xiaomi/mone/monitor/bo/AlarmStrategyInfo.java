package com.xiaomi.mone.monitor.bo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.dao.model.AppAlarmRule;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @author zhanggaofeng1
 */
@ToString
@Data
public class AlarmStrategyInfo {

    private int id;
    private Integer appId;
    private Integer iamId;
    private String appName;
    private Integer strategyType;
    private String strategyName;
    private String strategyDesc;
    private String creater;
    private long createTime;
    private long updateTime;
    private Integer status;//0可用，1不可用
    private String alertTeam;
    private List<AppAlarmRule> alarmRules;
    private boolean owner;//是否是拥有者
    private List<String> includeEnvs;
    private List<String> exceptEnvs;
    private List<String> includeServices;//包含service列表
    private List<String> exceptServices;//不包含service列表

    private List<String> includeModules;//包含模块列表

    private List<String> exceptModules;//不包含模块列表

    private List<Integer> includeFunctions;//包含函数列表

    private List<Integer> exceptFunctions;//不包含函数列表
    private List<String> alertMembers;//报警人员列表

    public void convertEnvList(String json){
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
            this.setIncludeFunctions(Arrays.asList(includeFunctions.split(",")).stream().map(Integer::parseInt).collect(Collectors.toList()));
        }

        if(jsonEnv.has("exceptFunctions")){
            String exceptFunctions = jsonEnv.get("exceptFunctions").getAsString();
            this.setExceptFunctions(Arrays.asList(exceptFunctions.split(",")).stream().map(Integer::parseInt).collect(Collectors.toList()));
        }

    }

    public static void main(String[] args) {
        List<String> strings = Arrays.asList("aaa".split(","));
        System.out.println(strings);
    }

}
