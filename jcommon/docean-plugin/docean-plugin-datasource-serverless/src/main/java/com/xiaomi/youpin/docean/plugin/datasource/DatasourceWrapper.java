package com.xiaomi.youpin.docean.plugin.datasource;

import lombok.Data;

import javax.sql.DataSource;
import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2022/3/28 14:50
 */
@Data
public class DatasourceWrapper implements Serializable {

    private String name;

    private DataSource ds;

}
