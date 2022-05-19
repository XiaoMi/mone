package com.xiaomi.youpin.docean.mvc;

import com.google.gson.JsonElement;
import lombok.Data;

import java.util.List;

/**
 * @author goodjava@qq.com
 */
@Data
public class WsRequest {

    private String path;

    private JsonElement params;
}
