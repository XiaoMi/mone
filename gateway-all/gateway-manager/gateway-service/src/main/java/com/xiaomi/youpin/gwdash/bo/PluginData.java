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
@Table("plugin_data")
@Data
public class PluginData {

    @Id
    private int id;

    @Column("plugin_id")
    private int pluginId;

    @Column
    private byte[] data;

    @Column
    private long ctime;

    @Column
    private long utime;

    @Column("commit_id")
    private String commitId;

    @Column("compile_id")
    private long compileId;

    @Column
    private String version;

    @Column
    private int stauts;

    @Column
    private String url;

    @Column
    private String tenant;

}
