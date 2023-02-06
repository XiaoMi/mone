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

import com.xiaomi.youpin.gwdash.dao.model.Dependency;
import com.xiaomi.youpin.gwdash.dao.model.ProjectGen;
import lombok.Data;
import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

/**
 * @author songyongchao1
 */
@Data
public class ProjectCreateMqBo {

    private long id;

    private String name;

    private String desc;

    private long ctime;

    private long utime;

    private int status;

    private String gitAddress;

    private String gitGroup;

    private String gitName;

    private ProjectGen projectGen;

    private int version;

    private int deployLimit;

    private boolean showAll;

    private String search;

    private List<Dependency> dependency;

    private String domain;

    private String mioneEnv;

    private Long iamTreeId;
}
