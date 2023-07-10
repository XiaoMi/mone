package com.xiaomi.mone.monitor.service.model.mifaas;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gaoxihui
 * @date 2022/8/17 5:25 下午
 */
@Data
public class ModuleInfo implements Serializable {
    String moduleName;
    Long funId;
    String funName;

    public ModuleInfo(String moduleName,Long funId,String funName){
        this.moduleName = moduleName;
        this.funId = funId;
        this.funName = funName;
    }
}
