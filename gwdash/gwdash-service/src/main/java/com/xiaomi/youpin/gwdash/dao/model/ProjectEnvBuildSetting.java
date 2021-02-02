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

import com.xiaomi.youpin.mischedule.enums.XmlSettingEnums;
import lombok.Data;
import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Data
@Table("project_env_build_setting")
public class ProjectEnvBuildSetting {
    @Id
    private long id;

    @Column("env_id")
    private long envId;

    @Column("build_dir")
    @ColDefine(width = 128)
    private String buildDir;

    @Column("jar_dir")
    @ColDefine(width = 128)
    private String jarDir;

    @Column("custom_params")
    @ColDefine(width = 128)
    private String customParams;

    /**
     * @see XmlSettingEnums
     */
    @Column("xml_setting")
    private int xmlSetting;
}
