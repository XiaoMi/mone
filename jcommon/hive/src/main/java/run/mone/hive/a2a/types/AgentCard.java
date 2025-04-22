package com.google.a2a.common.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AgentCard {
    /**
     * 表示代理提供者信息
     */
    public record AgentProvider(
        @JsonProperty("organization") String organization,
        @JsonProperty("url") String url
    ) {}

    /**
     * 表示代理能力
     */
    public record AgentCapabilities(
        @JsonProperty("streaming") Boolean streaming,
        @JsonProperty("pushNotifications") Boolean pushNotifications,
        @JsonProperty("stateTransitionHistory") Boolean stateTransitionHistory
    ) {
        public AgentCapabilities() {
            this(false, false, false);
        }
    }

    /**
     * 表示代理认证
     */
    public record AgentAuthentication(
        @JsonProperty("schemes") List<String> schemes,
        @JsonProperty("credentials") String credentials
    ) {}

    /**
     * 表示代理技能
     */
    public record AgentSkill(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("tags") List<String> tags,
        @JsonProperty("examples") List<String> examples,
        @JsonProperty("inputModes") List<String> inputModes,
        @JsonProperty("outputModes") List<String> outputModes
    ) {}

    /**
     * 表示代理卡片
     */
    public record AgentCardInfo(
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("url") String url,
        @JsonProperty("provider") AgentProvider provider,
        @JsonProperty("version") String version,
        @JsonProperty("documentationUrl") String documentationUrl,
        @JsonProperty("capabilities") AgentCapabilities capabilities,
        @JsonProperty("authentication") AgentAuthentication authentication,
        @JsonProperty("defaultInputModes") List<String> defaultInputModes,
        @JsonProperty("defaultOutputModes") List<String> defaultOutputModes,
        @JsonProperty("skills") List<AgentSkill> skills
    ) {
        public AgentCardInfo {
            if (defaultInputModes == null) {
                defaultInputModes = List.of("text");
            }
            if (defaultOutputModes == null) {
                defaultOutputModes = List.of("text");
            }
        }
    }
}