/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.docean.plugin.test.common;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.datasource.DatasourceConfig;

import java.beans.PropertyVetoException;

/**
 * @author goodjava@qq.com
 * @date 2020/7/2
 */
public class DbUtils {


    public static ComboPooledDataSource datasource(String statementInterceptors) throws PropertyVetoException {
        String user = "shop_x";
        String pwd = "grmtsH4jjSBceLPfLUkmL7eqvmOWGzwq";
        String ip = "10.38.163.168:9696";
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass("com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://"+ip+"/gateway_web?characterEncoding=utf8&useSSL=false&nnn=zzy";
        if (StringUtils.isNotEmpty(statementInterceptors)) {
            url = url + "&statementInterceptors=" + statementInterceptors;
        }
        dataSource.setJdbcUrl(url);
        dataSource.setUser(user);
        dataSource.setPassword(pwd);
        return dataSource;
    }

    public static ComboPooledDataSource datasource() throws PropertyVetoException {
        return datasource("");
    }


    public static DatasourceConfig datasourceConfig() {
        String user = "";
        String pwd = "";
        String ip = "";
        DatasourceConfig datasourceConfig = new DatasourceConfig();
        datasourceConfig.setDataSourcePasswd(ip);
        datasourceConfig.setDataSourceUrl("jdbc:mysql://"+ip+":9696/gateway_web?characterEncoding=utf8&useSSL=false");
        datasourceConfig.setDataSourceUserName(user);
        datasourceConfig.setName("dynamic");
        datasourceConfig.setDefaultMaxPoolSize(1);
        datasourceConfig.setDefaultMinPoolSize(1);
        datasourceConfig.setDefaultInitialPoolSize(1);
        return datasourceConfig;
    }

}
