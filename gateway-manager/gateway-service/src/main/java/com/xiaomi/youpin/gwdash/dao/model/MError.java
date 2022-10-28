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
import org.nutz.dao.entity.annotation.*;

/**
 * @author goodjava@qq.com
 * <p>
 * mione 错误记录表  目前当事件表适用
 */
@Data
@Table("m_error")
public class MError {

    @Id
    private int id;

    @Column
    @ColDefine(width = 40)
    private String ip = "";

    @Column(hump = true)
    @ColDefine(width = 50)
    private String serviceName = "";

    @Column(wrap = true)
    @ColDefine(width = 50)
    private String group = "";

    @Column
    private long utime;

    @Column
    private long ctime;

    /**
     * type = 1 qps 过高,需要扩容
     */
    @Column
    private int type;

    @Column
    private int count;

    @Column
    private int status;

    @Column(version = true)
    private int version;

    @Column
    @ColDefine(type = ColType.MYSQL_JSON)
    private ErrorContent content;


    @Column(wrap = true)
    private String key = "";


    public enum ErrorType {
        HealthCheck,
        //健康监测,负载过高
        HealthCheckLoadHigh,
        //需要nuke服务
        Nuke,
        //健康监测,负载太低了
        HealthCheckLoadLow

    }


}
