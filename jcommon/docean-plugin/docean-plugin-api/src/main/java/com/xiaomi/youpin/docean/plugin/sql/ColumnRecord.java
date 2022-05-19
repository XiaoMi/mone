package com.xiaomi.youpin.docean.plugin.sql;

import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 1/23/21
 */
@Data
public class ColumnRecord implements Serializable {

    private String name;

    private String data;

    private String type;

    private byte[] bytes;

}
