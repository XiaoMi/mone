package com.xiaomi.mone.log.manager.model.pojo;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.nutz.dao.entity.annotation.*;

@Table("milog_log_computer_room")
@Comment("机房")
@Data
public class MilogComputerRoomDO {
    @Id
    @Comment("主键Id")
    @ColDefine(customType = "bigint")
    private Long id;

    @Column(value = "room_name")
    @ColDefine(type = ColType.VARCHAR, width = 128)
    @Comment("机房名")
    private String roomName;

    @Column(value = "room_type")
    @ColDefine(type = ColType.INT)
    @Comment("机房类别")
    private Integer roomType;

    @Column(value = "ctime")
    @ColDefine(customType = "bigint")
    @Comment("创建时间")
    private Long ctime;

    @Column(value = "utime")
    @ColDefine(customType = "bigint")
    @Comment("更新时间")
    private Long utime;

}
