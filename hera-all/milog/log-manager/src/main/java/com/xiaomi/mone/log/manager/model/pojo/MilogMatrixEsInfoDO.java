package com.xiaomi.mone.log.manager.model.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaomi.mone.log.manager.model.BaseCommon;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

/**
 * @author zhangjuan
 * @date 2022-06-17
 */
@Data
@Comment("matrix es相关配置")
@TableName(value = "milog_matrix_esinfo")
public class MilogMatrixEsInfoDO extends BaseCommon {
    @Id
    @Comment("主键Id")
    @ColDefine(customType = "bigint")
    private Long id;

    @Column(value = "cluster")
    @ColDefine(type = ColType.VARCHAR)
    @Comment("日志配置集群")
    private String cluster;

    @Column(value = "es_catalog")
    @ColDefine(type = ColType.VARCHAR)
    @Comment("catalog")
    private String esCatalog;

    @Column(value = "es_database")
    @ColDefine(type = ColType.VARCHAR)
    @Comment("database：默认default")
    private String esDatabase;

    @Column(value = "es_token")
    @ColDefine(type = ColType.VARCHAR)
    @Comment("查询dt的token")
    private String esToken;
}
