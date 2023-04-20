package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

/**
 * @author zhangjuan
 * @Description 从数据工场查询 matrix es 域名的参数
 * @date 2022-06-17
 */
@Data
public class DTTableGetParamDTO {
    private String catalog;
    private String dbName;
    private String tableNameEn;
    private String esToken;
}
