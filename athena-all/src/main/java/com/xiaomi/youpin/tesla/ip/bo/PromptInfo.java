package com.xiaomi.youpin.tesla.ip.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author caobaoyu
 * @description:
 * @date 2023-05-17 18:33
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromptInfo implements Serializable {

    private Long id;

    private String promptName;

    private String meta;

    private String desc;

    private boolean collected;

    private int usedTimes;

    private List<Tag> tags;

    private Map<String, String> labels;

    private Map<String, String> userLabels;

    private String data;

    private Integer collectedSort;

    private List<String> addon;

    private List<ZPromptAddonItem> addon_metas;

    public boolean open(String key) {
        return this.labels.getOrDefault(key, "false").equals("true");
    }

    public boolean has(String key) {
        return this.labels.containsKey(key);
    }

    public boolean open(String key, String defaultValue) {
        return this.labels.getOrDefault(key, defaultValue).equals("true");
    }

    public String getLabelValue(String key, String defaultValue) {
        return this.labels.getOrDefault(key, defaultValue);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PromptInfo that)) return false;

        return Objects.equals(promptName, that.promptName);
    }

    @Override
    public int hashCode() {
        return promptName != null ? promptName.hashCode() : 0;
    }
}
