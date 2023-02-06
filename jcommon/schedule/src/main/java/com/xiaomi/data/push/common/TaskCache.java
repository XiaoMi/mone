package com.xiaomi.data.push.common;

import com.xiaomi.data.push.dao.model.TaskWithBLOBs;
import lombok.Data;

/**
 * @author goodjava@qq.com
 */
@Data
public class TaskCache {

    private TaskWithBLOBs task;

    private long time;

}
