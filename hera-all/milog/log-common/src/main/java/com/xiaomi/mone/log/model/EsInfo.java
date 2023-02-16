package com.xiaomi.mone.log.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EsInfo {
    private Long id;
    /**
     * ES地址
     */
    private String addr;

    /**
     * ES用户名
     */
    private String user;

    /**
     * ES密码
     */
    private String pwd;

    private String token;

    private String catalog;

    private String database;

    public EsInfo(Long id, String addr, String user, String pwd) {
        this.id = id;
        this.addr = addr;
        this.user = user;
        this.pwd = pwd;
    }

    public EsInfo(Long id, String addr, String token, String catalog, String database) {
        this.id = id;
        this.addr = addr;
        this.token = token;
        this.catalog = catalog;
        this.database = database;
    }
}
