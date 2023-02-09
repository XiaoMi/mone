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

public enum ProjectStatusEnum {

    CREATE(0, "create"),
    UPDATE(1, "update"),
    DELETE(2, "delete");

    private int id;
    private String status;

    public int getId () {return id;}

    public String getStatus() { return status; }

    ProjectStatusEnum(int id, String status) {
        this.id = id;
        this.status = status;
    }
}
