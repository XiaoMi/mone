package com.xiaomi.data.push.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 　@description: TODO
 * 　@author zhenghao
 *
 */
@Data
public class TaskHistoryData implements Serializable {

    private long time;

    private boolean result;

    private String message;

}
