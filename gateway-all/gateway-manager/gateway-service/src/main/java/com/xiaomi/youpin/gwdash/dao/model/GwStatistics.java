package com.xiaomi.youpin.gwdash.dao.model;

import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * 　 @description: TODO
 * 　 @author zhenghao
 *
 */
@Table("gw_statistics")
@Data
public class GwStatistics {

    @Id
    private long id;

    @Column
    private long ctime;

    @Column("gw_key")
    private String key;

    @Column("gw_value")
    private String value;

}