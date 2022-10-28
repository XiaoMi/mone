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
import org.nutz.dao.entity.annotation.*;

import java.util.Map;

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

    @Column("java_home")
    private String javaHome = "";

    @Column("image_name")
    private String imageName = "";

    @Column("build_cmd")
    private String buildCmd = "";

    @Column("docker_params")
    @ColDefine(type = ColType.MYSQL_JSON)
    private Map<String, String> dockerParams;

    /**
     * @see XmlSettingEnums
     */
    @Column("xml_setting")
    private int xmlSetting;
}
