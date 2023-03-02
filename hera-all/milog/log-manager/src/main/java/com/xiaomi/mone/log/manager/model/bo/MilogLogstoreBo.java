package com.xiaomi.mone.log.manager.model.bo;

import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import lombok.Data;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/1/14 16:41
 */
@Data
public class MilogLogstoreBo extends MilogLogStoreDO {
    private String logTypeText;
}
