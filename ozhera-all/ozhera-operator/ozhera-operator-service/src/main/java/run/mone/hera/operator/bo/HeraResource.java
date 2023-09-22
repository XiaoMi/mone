/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package run.mone.hera.operator.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import run.mone.hera.operator.service.IResource;
import run.mone.hera.operator.common.FileUtils;

import java.util.*;


/**
 * @author shanwb
 * @date 2023-02-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeraResource implements IResource {

    /**
     * Do you need to create based on YAML?
     */
    private Boolean needCreate = true;

    /**
     * Is the current resource required?
     */
    private Boolean required = true;

    /**
     * @see run.mone.hera.operator.common.ResourceTypeEnum#getTypeName()
     */
    private String resourceType;

    /**
     * Resource name, duplication not allowed.
     */
    private String resourceName;


    private String defaultYamlPath;
    /**
     * yaml
     */
    private String yamlStr;

    /**
     * Connection information for variable replacement in Nacos configuration.
     * e.g.
     * [{"key":"mysql.url", "value":"xxx"},
     * {"key":"mysql.password", "value":"yyy"}]
     */
    private List<Map<String, String>> connectionMapList;

    /**
     * Note
     */
    private String remark;

    /**
     * Expand configuration
     * e.g.
     * Nacos configuration：
     * key：dataId_#_group
     * value：config properties
     */
    private List<PropConf> propList;

    /**
     * Default extension configuration file path
     */
    private String[] defaultExtendConfigPath;

    @Override
    public String readResource(String path) {
        Objects.nonNull(path);
        return FileUtils.readResourceFile(path);
    }

    public void setDefaultYaml() {
        this.setYamlStr(this.readResource(defaultYamlPath));
    }

    public void setDefaultExtendConfig() {
        if (null == this.defaultExtendConfigPath || this.defaultExtendConfigPath.length == 0) {
            return;
        }

        if (null == propList) {
            propList = new ArrayList<>();
        }

        for (String p : this.defaultExtendConfigPath) {
            PropConf propConf = new PropConf();
            String key = p.substring(p.lastIndexOf("/") + 1, p.lastIndexOf("."));
            String config = this.readResource(p);
            propConf.setKey(key);
            propConf.setValue(config);
            propList.add(propConf);
        }
    }


}
