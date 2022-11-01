package org.apache.dubbo.rpc.gson;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * @author goodjava@qq.com
 */
public class IntTypeAdapter extends TypeAdapter<Integer> {

    @Override
    public void write(JsonWriter out,Integer value) throws IOException {
        out.value(value);
    }

    @Override
    public Integer read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        try {
            String result = in.nextString();
            if ("".equals(result)) {
                return null;
            }
            return Double.valueOf(result).intValue();
        } catch (NumberFormatException e) {
            throw new JsonSyntaxException(e);
        }
    }
}
