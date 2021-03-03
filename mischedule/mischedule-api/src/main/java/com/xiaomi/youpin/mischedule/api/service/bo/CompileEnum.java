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

package com.xiaomi.youpin.mischedule.api.service.bo;

/**
 * @author tsingfu
 */

public enum CompileEnum {

    CompileLog(1, "CompileLog", ""),
    CompileStatus(2, "CompileStatus", "");

    private int id;
    private String name;
    private String message;

    public int getId() { return id; }

    public String getName() { return name; }

    public String getMessage() {
        return message;
    }

    CompileEnum(int id, String name, String message) {
        this.id = id;
        this.name = name;
        this.message = message;
    };
}
