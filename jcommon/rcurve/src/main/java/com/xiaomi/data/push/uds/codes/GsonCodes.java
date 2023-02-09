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

package com.xiaomi.data.push.uds.codes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import run.mone.api.Cons;

import java.lang.reflect.Type;

/**
 * @author goodjava@qq.com
 * @date 1/22/21
 */
public class GsonCodes implements ICodes {

    public static final byte type = CodeType.GSON;

    private Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setDateFormat(Cons.EFFAULT_DATE_STYLE).create();

    @Override
    public <T> T decode(byte[] data, Type type) {
        return gson.fromJson(new String(data), type);
    }

    @Override
    public <T> byte[] encode(T t) {
        return gson.toJson(t).getBytes();
    }

    @Override
    public byte type() {
        return type;
    }
}
