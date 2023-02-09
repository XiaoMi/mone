package com.xiaomi.data.push.uds.context;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2022/11/16 21:27
 */
@Data
@AllArgsConstructor
public class TraceEvent implements Serializable {

    private String type;

    private long useTime;

}
