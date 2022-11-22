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

package com.xiaomi.youpin.gwdash.bo;

import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * @author goodjava@qq.com
 */
@Data
@Table("tesla_ds")
public class TeslaDs {

    @Id
    private int id;

    @Column
    private int type;

    @Column("driver_class")
    private String driverClass;

    @Column("data_source_url")
    private String dataSourceUrl;

    @Column("user_name")
    private String userName;

    @Column("pass_wd")
    private String passWd;

    @Column("pool_size")
    private int poolSize;

    @Column("max_pool_size")
    private int maxPoolSize;

    @Column("min_pool_size")
    private int minPoolSize;

    @Column
    private String name;

    @Column
    private int state;

    @Column
    private long ctime;

    @Column
    private long utime;

    @Column
    private String creator;

    @Column("jar_path")
    private String jarPath;

    @Column("ioc_package")
    private String iocPackage;

    @Column("app_name")
    private String appName;

    @Column("reg_address")
    private String regAddress;

    @Column("api_package")
    private String apiPackage;

    @Column("threads")
    private int threads;

    @Column("redis_type")
    private String redisType;

    @Column("nacos_data_id")
    private String nacosDataId;

    @Column("nacos_group")
    private String nacosGroup;

    @Column("mongo_database")
    private String mongoDatabase;

    @Column
    private String description;

}
