package com.xiaomi.mone.monitor.bo;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gaoxihui
 * @date 2021/12/2 5:27 下午
 */
public enum PlatFormType {
    open(0,"open","open",PlatForm.open,"开源组",0),
    china(0,"china","china",PlatForm.mione,"中国区",0),
    informationDept(1,"informationDept","china",PlatForm.informationDept,"信息部",1),
    youpin(2,"youpin","youpin",PlatForm.mione,"有品",0),
    deployment(6,"deployment","cloud",PlatForm.cloud,"云平台(deployment)",6),
    mice(7,"mice","cloud",PlatForm.cloud,"云平台(mice)",7),
    matrix(8,"matrix","cloud",PlatForm.cloud,"云平台(matrix)",8),
    ocean(9,"ocean","cloud",PlatForm.cloud,"云平台(ocean)",9),
    neo(10,"neo","cloud",PlatForm.cloud,"云平台(neo)",10),
    ;

    private Integer code;
    private String name;
    private String grafanaDir;
    private PlatForm platForm;
    private String desc;
    private Integer marketCode;

    private static List<Integer> cloudPlatCodes = Lists.newArrayList(PlatFormType.deployment.getCode(),
            PlatFormType.mice.getCode(),PlatFormType.matrix.getCode(),PlatFormType.ocean.getCode(),PlatFormType.neo.getCode());


    PlatFormType(Integer code, String name, String grafanaDir,PlatForm platForm,String desc,Integer marketCode) {
        this.code = code;
        this.name = name;
        this.grafanaDir = grafanaDir;
        this.platForm = platForm;
        this.desc = desc;
        this.marketCode = marketCode;
    }

    public Integer getCode() {
        return code;
    }

    public Integer getMarketCode(){
        return marketCode;
    }

    public String getName() {
        return name;
    }

    public String getGrafanaDir() {
        return grafanaDir;
    }

    public PlatForm getPlatForm() {
        return platForm;
    }

    public String getDesc() {
        return desc;
    }

    public static PlatFormType getEnum(Integer code){
        PlatFormType[] values = PlatFormType.values();
        for(PlatFormType value : values){
            if(value.getCode().equals(code)){
                return value;
            }
        }
        return null;
    }

    public static String getDescByCode(Integer code){
        PlatFormType[] values = PlatFormType.values();
        for(PlatFormType value : values){
            if(value.getCode().equals(code)){
                return value.getDesc();
            }
        }
        return null;
    }

    public static String getGrafanaDirByCode(Integer code){

        PlatFormType[] values = PlatFormType.values();
        for(PlatFormType value : values){
            if(value.getCode().equals(code)){
                return value.getGrafanaDir();
            }
        }
        return null;
    }

    public static Integer getCodeByGrafanaDir(String grafanaDir){
        PlatFormType[] values = PlatFormType.values();
        for(PlatFormType value : values){
            if(value.getGrafanaDir().equals(grafanaDir)){
                return value.getCode();
            }
        }
        return null;
    }

    public static boolean isCodeBlondToPlatForm(Integer code,PlatForm platForm){

        PlatFormType[] values = PlatFormType.values();
        for(PlatFormType value : values){
            if(value.getCode().equals(code) && value.getPlatForm().equals(platForm)){
                return true;
            }
        }
        return false;
    }

    public static List<Pair> getCodeDescList(){
        List <Pair> list = new ArrayList<>();
        PlatFormType[] values = PlatFormType.values();
        for(PlatFormType value : values){
            Pair pair = new Pair(value.getCode(),value.getDesc());
            list.add(pair);
        }
        return list;
    }

    //检查是否是合法类型
    public static Integer getMarketType(Integer code){
        PlatFormType[] values = PlatFormType.values();
        for(PlatFormType value : values){
            if(value.getCode().equals(code)){
                //通过code 给marketCode
                return value.getMarketCode();
            }
        }
        return -1;
    }

    public static boolean isCloudPlatForm(Integer code){
        if(code == null){
            return false;
        }
        return cloudPlatCodes.contains(code);
    }

}
