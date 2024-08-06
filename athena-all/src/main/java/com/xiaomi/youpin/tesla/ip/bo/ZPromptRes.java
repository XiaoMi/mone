package com.xiaomi.youpin.tesla.ip.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author caobaoyu
 * @description: Z平台http返回
 * @date 2023-04-21 15:51
 */
@Data
public class ZPromptRes implements Serializable {

    private Long id;

    private String name;

    private String data;

    private int type;

    private String info;

    private String meta;

    private Integer mode;

    private String description;

    private List<Tag> tags;

    private Boolean collected;

    private int usedTimes;

    private Map<String, String> labels;

    private Map<String, String> userLabels;

    private Integer collectedSort;

    private List<String> addon;
    private List<ZPromptAddonItem> addon_metas;
}
