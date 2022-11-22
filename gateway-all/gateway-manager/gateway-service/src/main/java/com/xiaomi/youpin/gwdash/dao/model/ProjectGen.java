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

import java.io.Serializable;
import java.util.List;

/**
 * @author gaoyibo
 */
@Data
public class ProjectGen implements Serializable {
    private String type;
    private String gitAddress;
    private String projectName;
    private String groupId;
    private String author = "";
    private String packageName;

    private String gitGroup;
    private String gitName;

    private int dubboVersion = 2;

    /**
     * 代码生成使用的授权帐号
     */
    private String gitUser;
    private String gitToken;

    /**
     * 是否生成代码
     */
    private boolean gen;


    /**
     * 是否集成tomcat
     */
    private boolean needTomcat;


    /**
     * for filter
     */
    private String versionId;

    private String filterOrder;

    private String params = "[]";

    private String cname;

    private String desc = "";

    private String isSystem  = "0";


    /**
     * for plugin
     */
    private String projectPath;

    private String url;

    private String version;

    /**
     * generate code in git
     */
    private boolean git;

    /**
     * for dependency
     */
    private List<Dependency> dependency;
}
