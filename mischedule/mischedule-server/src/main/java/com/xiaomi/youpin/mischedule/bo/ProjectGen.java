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

package com.xiaomi.youpin.mischedule.bo;

import lombok.Data;

/**
 * @author gaoyibo
 */
@Data
public class ProjectGen {

    /**
     * spring filter plugin
     */
    private String type;

    private String gitAddress;
    private String projectName;
    private String groupId;
    private String author = "";
    private String packageName;
    private int dubboVersion;

    /**
     * 代码生成使用的授权帐号
     */
    private String gitUser;
    private String gitToken;

    /**
     * 是否集成tomcat
     */
    private boolean needTomcat;


    /**
     * for filter
     */
    private String versionId;

    private String filterOrder;

    private String params;

    private String cname;

    private String desc;

    private String isSystem;


    /**
     * for plugin
     */
    private String projectPath;

    private String url;

}
