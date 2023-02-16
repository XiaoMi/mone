package com.xiaomi.mone.monitor.bo;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gaoxihui
 * @date 2021/12/2 5:27 下午
 */
public enum PlatFormType {
    open(0,"open","open",PlatForm.open,"开源组",0)
    ;

    private Integer code;
    private String name;
    private String grafanaDir;
    private PlatForm platForm;
    private String desc;
    private Integer marketCode;


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
        return false;
    }

}
