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

import com.xiaomi.youpin.gwdash.dao.model.ApiGroupInfo;
import com.xiaomi.youpin.hermes.bo.ResourceBo;
import com.xiaomi.youpin.hermes.entity.Group;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class AccountDetailResult {

    private Long id;

    private String userName;

    private String name;

    private Integer role;

    private String gid;

    private String email;

    private String phone;

    private Long ctime;

    private Long utime;

    private List<Group> gidInfos;

    private List<ResourceBo> resources;

}
