package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

/**
 * @author zhangjuan
 * @Description 查询 matrix es 数据的参数
 * @date 2022-06-17
 */
@Data
public class EsSearchParamDTO {
    private String esDomain;
    private String index;
    private String catalog;
    private String dbName;
    private String esToken;

    public EsSearchParamDTO(String esDomain, String index, String catalog, String dbName, String esToken) {
        this.index = index;
        this.esDomain = esDomain;
        this.catalog = catalog;
        this.dbName = dbName;
        this.esToken = esToken;
    }
}
