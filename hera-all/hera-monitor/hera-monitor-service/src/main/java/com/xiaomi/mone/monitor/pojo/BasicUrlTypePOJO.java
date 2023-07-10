package com.xiaomi.mone.monitor.pojo;

import com.google.gson.JsonObject;
import lombok.Data;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/4/20 3:40 PM
 */
@Data
public class BasicUrlTypePOJO {

    private String name;

    private JsonObject reqJsonObject;
}
