package com.xiaomi.mone.app.api.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class HeraMetaDataQuery implements Serializable {


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


}
