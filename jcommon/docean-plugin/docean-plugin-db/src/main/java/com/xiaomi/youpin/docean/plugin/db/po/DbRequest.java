package com.xiaomi.youpin.docean.plugin.db.po;

import lombok.Builder;
import lombok.Data;
import org.nutz.dao.entity.Entity;

/**
 * @author goodjava@qq.com
 * @date 2/28/21
 */
@Data
@Builder
public class DbRequest {

    private String dsName;

    private String type;

    private String sql;

    private String[] params;

    private Entity entity;

    private String datasource;

}
