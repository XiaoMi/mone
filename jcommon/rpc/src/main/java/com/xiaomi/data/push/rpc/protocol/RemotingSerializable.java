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

package com.xiaomi.data.push.rpc.protocol;

import com.google.gson.Gson;

import java.nio.charset.Charset;


/**
 * @author goodjava@qq.com
 */
public abstract class RemotingSerializable {

    public final static Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    public static byte[] encode(final Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj).getBytes();
    }

    public static String toJson(final Object obj, boolean prettyFormat) {
        return new Gson().toJson(obj);
    }

    public static <T> T decode(final byte[] data, Class<T> classOfT) {
        final String json = new String(data, CHARSET_UTF8);
        return new Gson().fromJson(json, classOfT);
    }

    public String toJson() {
        return toJson(false);
    }

    public String toJson(final boolean prettyFormat) {
        return toJson(this, prettyFormat);
    }
}
