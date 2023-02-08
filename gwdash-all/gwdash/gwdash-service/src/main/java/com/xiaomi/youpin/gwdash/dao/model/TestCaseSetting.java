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


import com.xiaomi.youpin.gwdash.bo.TestCaseParam;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

@Data
@Table("mione_test_case_setting")
public class TestCaseSetting {
    @Id
    private long id;

    @Column("service_name")
    private String serviceName;

    @Column
    private String method;

    @Column("test_case_param")
    @ColDefine(type = ColType.MYSQL_JSON)
    private TestCaseParam testCaseParam;

    @Column
    private long ctime;

    @Column
    private long utime;

    @Column(version = true)
    private int version;
}
