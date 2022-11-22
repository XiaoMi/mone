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

package com.xiaomi.youpin.gwdash.dao.model.transfer;

import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;


@Table("api_info")
@Data
public class ApiInfoTransfer {
    @Id(auto = false)
    private Long id;

    @org.nutz.dao.entity.annotation.Column("name")
    private String name;

    @org.nutz.dao.entity.annotation.Column("description")
    private String description;

    @org.nutz.dao.entity.annotation.Column("url")
    private String url;

    @org.nutz.dao.entity.annotation.Column("http_method")
    private String httpMethod;

    @org.nutz.dao.entity.annotation.Column("path")
    private String path;

    @org.nutz.dao.entity.annotation.Column("route_type")
    private Integer routeType;

    @org.nutz.dao.entity.annotation.Column("group_id")
    private Integer groupId;

    @org.nutz.dao.entity.annotation.Column("service_name")
    private String serviceName;

    @org.nutz.dao.entity.annotation.Column("method_name")
    private String methodName;

    @org.nutz.dao.entity.annotation.Column("service_group")
    private String serviceGroup;

    @org.nutz.dao.entity.annotation.Column("service_version")
    private String serviceVersion;

    @org.nutz.dao.entity.annotation.Column("status")
    private Integer status;

    @org.nutz.dao.entity.annotation.Column("creator")
    private String creator;

    @org.nutz.dao.entity.annotation.Column("updater")
    private String updater;

    @org.nutz.dao.entity.annotation.Column("content_type")
    private String contentType;

    @org.nutz.dao.entity.annotation.Column("flag")
    private Integer flag;

    @org.nutz.dao.entity.annotation.Column("invoke_limit")
    private Integer invokeLimit;

    @org.nutz.dao.entity.annotation.Column("qps_limit")
    private Integer qpsLimit;

    @org.nutz.dao.entity.annotation.Column("timeout")
    private Integer timeout;

    @org.nutz.dao.entity.annotation.Column("cache_expire")
    private Integer cacheExpire;

    @org.nutz.dao.entity.annotation.Column("token")
    private String token;

    @org.nutz.dao.entity.annotation.Column("ctime")
    private Long ctime;

    @org.nutz.dao.entity.annotation.Column("utime")
    private Long utime;

    @org.nutz.dao.entity.annotation.Column("plugin_name")
    private String pluginName;

    @org.nutz.dao.entity.annotation.Column("ds_ids")
    private String dsIds;

    @org.nutz.dao.entity.annotation.Column("ip_anti_brush_limit")
    private Integer ipAntiBrushLimit;

    @org.nutz.dao.entity.annotation.Column("uid_anti_brush_limit")
    private Integer uidAntiBrushLimit;

    @org.nutz.dao.entity.annotation.Column("priority")
    private Integer priority;

    @org.nutz.dao.entity.annotation.Column("application")
    private String application;

    @org.nutz.dao.entity.annotation.Column("param_template")
    private String paramTemplate;

    @org.nutz.dao.entity.annotation.Column("filter_params")
    private String filterParams;

    @org.nutz.dao.entity.annotation.Column("attachment")
    private String attachment;

    @org.nutz.dao.entity.annotation.Column("app_src")
    private Integer appSrc;

    @org.nutz.dao.entity.annotation.Column("api_src")
    private Integer apiSrc;

    @org.nutz.dao.entity.annotation.Column("tenement")
    private String tenement;

}
