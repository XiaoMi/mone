package com.xiaomi.mone.monitor.bo;

/**
 * @author gaoxihui
 * @date 2021/12/2 5:27 下午
 */
public enum PlatFormType {
    open(0,"open","open", PlatForm.open,"开源组",0),
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

    public static Integer getCodeByName(String name){
        PlatFormType[] values = PlatFormType.values();
        for(PlatFormType value : values){
            if(value.getName().equals(name)){
                return value.getCode();
            }
        }
        return null;
    }

}
