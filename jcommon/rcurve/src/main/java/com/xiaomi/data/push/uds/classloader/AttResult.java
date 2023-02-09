package com.xiaomi.data.push.uds.classloader;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/2/2 18:30
 *
 * 带附加值的Result
 */
@Data
public class AttResult implements Serializable {

    private Object res;

    private Map<String,String> attachments = new HashMap<>();

}
