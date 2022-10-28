package com.xiaomi.youpin.gwdash.dao.model;

import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;
import com.xiaomi.youpin.gwdash.common.MetaDataTypeEnum;

@Data
@Table("metadata")
public class MetaData {
    @Id
    private int id;

    @Column
    private String name;

    @Column
    private String description;

    @Column("refer_header")
    private String referHeader;

    /**
     * @see MetaDataTypeEnum
     */
    @Column
    private int type;

    @Column
    private long ctime;

    @Column
    private long utime;

    @Column("tenant")
    private String tenement;
}
