package com.xiaomi.data.push.schedule.task.graph;

import lombok.Data;

/**
 *
 * @author zhangzhiyong
 * @date 08/06/2018
 */
@Data
public class TaskData {

    private String id;

    public TaskData(String id) {
        this.id = id;
    }

}
