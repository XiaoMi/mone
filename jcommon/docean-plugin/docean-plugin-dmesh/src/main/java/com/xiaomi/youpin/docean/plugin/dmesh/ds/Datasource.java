package com.xiaomi.youpin.docean.plugin.dmesh.ds;

import lombok.Data;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

/**
 * @author goodjava@qq.com
 */
@Data
public class Datasource {

    private String name;

    private String dsType;

    private String driverClass;

    private Integer defaultInitialPoolSize;

    private Integer defaultMaxPoolSize;

    private Integer defaultMinPoolSize;

    private String dataSourceUrl;

    private String dataSourceUserName;

    private String dataSourcePasswd;

    private String hosts;

    private String type;


    @SneakyThrows
    public void set(String key, String value) {
        Class<? extends Datasource> clazz = this.getClass();
        Field field = clazz.getDeclaredField(key);
        field.setAccessible(true);
        if (field.getType().equals(Integer.class)) {
            field.set(this, Integer.valueOf(value).intValue());
        } else {
            field.set(this, value);
        }
    }
}
