package com.google.a2a.common.types;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthenticationInfos {
    /**
     * 表示认证信息
     */
    public record AuthenticationInfo(
        @JsonProperty("schemes") List<String> schemes,
        @JsonProperty("credentials") String credentials,
        Map<String, Object> additionalProperties
    ) {
        public AuthenticationInfo(List<String> schemes, String credentials) {
            this(schemes, credentials, new HashMap<>());
        }
        
        public AuthenticationInfo {
            if (additionalProperties == null) {
                additionalProperties = new HashMap<>();
            }
        }
        
        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return additionalProperties;
        }
        
        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            additionalProperties.put(name, value);
        }
    }

    /**
     * 表示推送通知配置
     */
    public record PushNotificationConfig(
        @JsonProperty("url") String url,
        @JsonProperty("token") String token,
        @JsonProperty("authentication") AuthenticationInfo authentication
    ) {
        public PushNotificationConfig(String url, String token) {
            this(url, token, null);
        }
    } 
}