package com.xiaomi.mone.log.manager.model.dto;

import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import lombok.Data;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/12/17 11:38
 */
@Data
public class LogStoreDTO extends MilogLogStoreDO {
    /**
     * 日志类型名称中文
     */
    private String logTypeName;
    /**
     * 机房名称中文
     */
    private String machineRoomName;
    /**
     * 是否选择自定义索引
     */
    private Boolean selectCustomIndex;

    private Long esResourceId ;

}
