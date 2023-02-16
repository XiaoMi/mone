package com.xiaomi.mone.log.manager.model.pojo;

import com.xiaomi.mone.log.manager.model.BaseCommon;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

/**
 * @author zhangping17
 * @date 2021-10-15 Region,AZ实体
 */
@Table("milog_region_zone")
@Comment("milog区域可用区")
@Data
public class MilogRegionAvailableZoneDO extends BaseCommon {
    @Id
    @Comment("主键Id")
    @ColDefine(customType = "bigint")
    private Long id;

    @Column(value = "region_name_en")
    @ColDefine(type = ColType.VARCHAR)
    @Comment("region英文名")
    private String regionNameEN;

    @Column(value = "region_name_cn")
    @ColDefine(type = ColType.VARCHAR)
    @Comment("region中文名")
    private String regionNameCN;

    @Column(value = "zone_name_en")
    @ColDefine(type = ColType.VARCHAR)
    @Comment("zone英文名")
    private String zoneNameEN;

    @Column(value = "zone_name_cn")
    @ColDefine(type = ColType.VARCHAR)
    @Comment("zone中文名")
    private String zoneNameCN;
}
