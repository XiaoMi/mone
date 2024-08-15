package com.xiaomi.youpin.tesla.ip.bo.robot;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/12/11 17:20
 */
@Data
@Builder
public class MapData implements Serializable, MessageData {

    private Map<String, String> map;

    @Builder.Default
    private Map<String, MapDataValue> memaryMap = new HashMap<>();

    @Override
    public String toString() {
        return this.map.toString();
    }
}
