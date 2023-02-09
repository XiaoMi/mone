package com.xiaomi.data.push.schedule.task.impl.sql;

import lombok.Data;

/**
 * @author goodjava@qq.com
 */
@Data
public class SqlTaskParam {

    private String sql;
    private String username;
    private String passwd;
    private String url;
    private String params;
    private String type;

    private String driverClassName;


}
