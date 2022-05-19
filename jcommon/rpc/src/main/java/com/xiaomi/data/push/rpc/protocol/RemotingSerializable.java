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
