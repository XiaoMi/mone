package com.xiaomi.mone.tpc.dao.entity;

import lombok.Data;
import lombok.ToString;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 9:39
 */
@ToString(callSuper = true)
@Table("account_entity")
@Data
public class AccountEntity extends BaseEntity{


    @Column("account")
    private String account;

    @Column("pwd")
    private String pwd;

    @Column("name")
    private String name;

}
