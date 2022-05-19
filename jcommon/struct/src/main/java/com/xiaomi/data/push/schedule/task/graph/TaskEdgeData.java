package com.xiaomi.data.push.schedule.task.graph;

import lombok.Data;

/**
 *
 * @author zhangzhiyong
 * @date 08/06/2018
 */
@Data
public class TaskEdgeData {

    private int from;
    private int to;

    public TaskEdgeData(int from, int to) {
        this.from = from;
        this.to = to;
    }

}
