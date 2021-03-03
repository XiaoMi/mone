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

package com.xiaomi.youpin.quota.bo;

import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
@Table("resource")
@Data
public class Resource {

    @Id
    private int id;

    @Column
    private String type;

    @Column
    private String ip;

    @Column
    private String name;

    @Column("host_name")
    private String hostName;

    /**
     * cpu 核心数量
     */
    @Column("cpu")
    private int cpu;

    /**
     *  总价格，单位分
     */
    @Column("price")
    private long price;

    @Column("remain_cpu")
    private int remainCpu;

    /**
     * 系统占用的CPU
     */
    @Column("system_cpu")
    @ColDefine(type = ColType.MYSQL_JSON)
    private List<Integer> systemCpu;



    @Column("biz_ids")
    @ColDefine(type = ColType.MYSQL_JSON)
    private Map<Long, BizResource> bizIds;


    /**
     * 标签
     */
    @Column("labels")
    @ColDefine(type = ColType.MYSQL_JSON)
    private Map<String, String> labels;

    @Column("mem")
    private long mem;

    @Column("system_mem")
    private long systemMem;

    @Column("remain_mem")
    private long remainMem;

    @Column("ports")
    @ColDefine(type = ColType.MYSQL_JSON)
    private List<Integer> ports;

    @Column("project_ids")
    @ColDefine(type = ColType.MYSQL_JSON)
    private List<Long> projectIds;

    /**
     * 系统占用的端口
     */
    @Column("system_ports")
    @ColDefine(type = ColType.MYSQL_JSON)
    private List<Integer> systemPorts;

    /**
     * 优先owner
     */
    @Column("owners")
    @ColDefine(type = ColType.MYSQL_JSON)
    private List<String> owners;

    /**
     * 资源level
     */
    @Column("level")
    private int level;

    /**
     * 机器负载
     */
    @Column("load_average")
    private double loadAverage;

    /**
     * 是否支持keycenter, 0不支持，1支持
     */
    @Column("support_key_center")
    private int supportKeyCenter;

    /**
     * 是否应用独占, 0不独占，1独占
     */
    @Column("is_oneapp")
    private int isOneApp;

    @Column
    private long ctime;

    @Column
    private long utime;

    @Column
    private int version;

    /**
     * 机器状态，0在线，1下线
     */
    @Column
    private int status;

    @Column
    private int rorder;


}
