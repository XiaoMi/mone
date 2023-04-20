package com.xiaomi.mone.log.manager.model.pojo;

import com.xiaomi.mone.log.manager.model.BaseCommon;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/7/14 16:01
 */
@Table("milog_store_space_auth")
@Comment("milog store授权表,store可绑定额外的space")
@Data
public class MilogStoreSpaceAuth extends BaseCommon {
    @Id
    @Comment("主键Id")
    @ColDefine(customType = "bigint")
    private Long id;

    @Column(value = "store_id")
    @ColDefine(customType = "bigint")
    @Comment("store主键")
    private Long storeId;

    @Column(value = "space_id")
    @ColDefine(customType = "bigint")
    @Comment("space主键")
    private Long spaceId;

}
