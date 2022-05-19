package com.xiaomi.youpin.docean.plugin.sql;

import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 3/1/21
 */
@Data
public class ColumnInfo {

    private String name;

    private String type;

    public ColumnInfo(String name, String type) {
        this.name = name;
        this.type = type;
    }
}
