package com.xiaomi.mone.monitor.service.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gaoxihui
 * @date 2022/3/20 10:32 上午
 */
@Data
public class HeraAppBaseQuery implements Serializable {

    private Integer id;

    private String appId;

    private String appName;

    private String appCname;

    private Integer iamTreeId;

    private Integer bindType;

    private Integer platformType;

    private Integer appType;

    private String appLanguage;

    private String envsMapping;

    private Integer status;

    private String participant;

    private String myParticipant;

    private Integer page;
    
    private Integer pageSize;

    private Integer offset;
    
    private Integer limit;
    
    public void initPageParam(){

        if(getPage() == null || getPage() <=0){
            setPage(1);
        }
        if(getPageSize() == null || getPageSize().intValue() <=0){
            setPageSize(10);
        }
        
        setOffset((page -1) * pageSize);
        setLimit(pageSize);
    }

    private String orderByClause;


}
