package com.xiaomi.data.push.uds.context;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2022/10/17 11:07
 */
@Data
public class CallContext implements Serializable {

    private Map<String, String> attrs = new HashMap<>(1);

    public void put(String key, String value) {
        this.attrs.put(key, value);
    }

}
