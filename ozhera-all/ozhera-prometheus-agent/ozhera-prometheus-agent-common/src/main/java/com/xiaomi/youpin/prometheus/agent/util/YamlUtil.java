package com.xiaomi.youpin.prometheus.agent.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringWriter;

@Slf4j
public class YamlUtil {
    /**
     * Convert YAML string to class object
     *
     * @param yamlStr String
     * @param clazz Target class
     * @param <T> Generic
     * @return Target class
     */

    public static Object obj = new Object();

    public static synchronized <T> T toObject(String yamlStr, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        try {
            return mapper.readValue(yamlStr, clazz);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        return null;

    }

    /**
     * Convert class object to YAML string
     *
     * @param object Object
     * @return YAML string
     */
    public static synchronized String toYaml(Object object) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        StringWriter stringWriter = new StringWriter();
        try {
            mapper.writeValue(stringWriter, object);
            return stringWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
