package com.xiaomi.mone.tpc.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.micrometer.core.instrument.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/4/7 16:35
 */
public class JacksonUtil {
    private static final Logger log = LoggerFactory.getLogger(JacksonUtil.class);
    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public JacksonUtil() {
    }

    public static Module builderJacksonModule() {
        SimpleModule simpleModule = new SimpleModule();
        final DateTimeFormatter ftf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        JsonSerializer<ZonedDateTime> jsonSerializerZonedDateTime = new JsonSerializer<ZonedDateTime>() {
            @Override
            public void serialize(ZonedDateTime zonedDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(zonedDateTime.format(DateTimeFormatter.ISO_INSTANT));
            }
        };
        JsonSerializer<LocalDateTime> jsonSerializerDateTime = new JsonSerializer<LocalDateTime>() {
            @Override
            public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(localDateTime.format(ftf));
            }
        };
        JsonSerializer<LocalDate> jsonSerializerDate = new JsonSerializer<LocalDate>() {
            @Override
            public void serialize(LocalDate localDate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(localDate.format(ftf));
            }
        };
        JsonDeserializer<ZonedDateTime> jsonDeserializerZonedDateTime = new JsonDeserializer<ZonedDateTime>() {
            @Override
            public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                return StringUtils.isNotEmpty(jsonParser.getValueAsString()) ? ZonedDateTime.parse(jsonParser.getValueAsString()) : null;
            }
        };
        JsonDeserializer<LocalDateTime> jsonDeserializerDateTime = new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                return StringUtils.isNotEmpty(jsonParser.getValueAsString()) ? LocalDateTime.parse(jsonParser.getValueAsString(), ftf) : null;
            }
        };
        JsonDeserializer<LocalDate> jsonDeserializerDate = new JsonDeserializer<LocalDate>() {
            @Override
            public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                return StringUtils.isNotEmpty(jsonParser.getValueAsString()) ? LocalDate.parse(jsonParser.getValueAsString(), ftf) : null;
            }
        };
        simpleModule.addSerializer(ZonedDateTime.class, jsonSerializerZonedDateTime);
        simpleModule.addSerializer(LocalDateTime.class, jsonSerializerDateTime);
        simpleModule.addSerializer(LocalDate.class, jsonSerializerDate);
        simpleModule.addDeserializer(ZonedDateTime.class, jsonDeserializerZonedDateTime);
        simpleModule.addDeserializer(LocalDateTime.class, jsonDeserializerDateTime);
        simpleModule.addDeserializer(LocalDate.class, jsonDeserializerDate);
        return simpleModule;
    }

    public static String bean2Json(Object obj) {
        String json = null;

        try {
            json = OBJECT_MAPPER.writeValueAsString(obj);
        } catch (Exception var3) {
            log.error("bean2Json error", var3);
        }

        return json;
    }

    public static <T> T json2Bean(String jsonStr, Class<T> objClass) {
        Object obj = null;

        try {
            obj = OBJECT_MAPPER.readValue(jsonStr, objClass);
        } catch (Exception var4) {
            log.error("json2Bean error", var4);
        }

        return (T)obj;
    }

    public static <T> T json2TypeReference(String jsonStr, TypeReference<T> valueTypeRef) {
        Object obj = null;

        try {
            obj = OBJECT_MAPPER.readValue(jsonStr, valueTypeRef);
        } catch (Exception var4) {
            log.error("json2TypeReference error", var4);
        }

        return (T)obj;
    }

    static {
        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        OBJECT_MAPPER.registerModule(builderJacksonModule());
    }
}

