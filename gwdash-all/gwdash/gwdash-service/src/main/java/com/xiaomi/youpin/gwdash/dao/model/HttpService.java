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

package com.xiaomi.youpin.gwdash.dao.model;

import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * @author goodjava@qq.com
 *
 * nginx上边需要观察的服务
 *
 */
@Data
@Table("nginx_service")
public class HttpService {

    @Id
    private long id;

    @Column("service_name")
    private String serviceName;

    @Column
    private long ctime;

    @Column
    private long utime;

    @Column
    private int status;

    @Column("upstream_name")
    private String upstreamName;

    @Column("my_group")
    private String group;

    /**
     * 配置文件的位置
     */
    @Column("config_path")
    private String configPath;


    public static final int STATUS_ON = 1;
    public static final int STATUS_DELETE = 0;



}
