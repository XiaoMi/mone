package com.xiaomi.miapi.bo;

import lombok.Data;

import java.util.List;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class LayerItem {
    private String itemName;
    private String itemClassStr;
    private String itemTypeStr;
    private boolean required;
    private String desc;
    private String defaultValue;
    private String exampleValue;
    private String[] allowableValues;
    private List<LayerItem> itemValue;
}
