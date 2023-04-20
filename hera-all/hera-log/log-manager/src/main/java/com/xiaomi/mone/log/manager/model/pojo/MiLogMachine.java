package com.xiaomi.mone.log.manager.model.pojo;

import lombok.Data;
import org.nutz.dao.entity.annotation.*;

/**
 * @author wtt
 * @version 1.0
 * @description 机器信息
 * @date 2021/7/16 11:26
 */
@Table("milog_machine")
@Comment("milog中机器信息")
@Data
public class MiLogMachine {
    @Id
    @Comment("主键Id")
    @ColDefine(customType = "bigint")
    private Long id;

    @Column(value = "ctime")
    @ColDefine(customType = "bigint")
    @Comment("创建时间")
    private Long ctime;

    @Column(value = "utime")
    @ColDefine(customType = "bigint")
    @Comment("更新时间")
    private Long utime;

    @Column(value = "type")
    @ColDefine(type = ColType.INT)
    @Comment("机器解析类型：1:agent，2.stream")
    private Integer type;

    @Column(value = "ip")
    @ColDefine(type = ColType.TEXT)
    @Comment("机器ip")
    private String ip;

    @Column(value = "creator")
    @ColDefine(type = ColType.VARCHAR, width = 128)
    @Comment("创建者")
    private String creator;

    @Column(value = "description")
    @ColDefine(type = ColType.VARCHAR, width = 1024)
    @Comment("备注说明")
    private String description;
}
