package com.xiaomi.data.push.schedule.task.impl.interceptor;

import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskInterceptor;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author zhangzhiyong
 * @date 10/06/2018
 */
@Component
public class DemoInterceptor implements TaskInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(DemoInterceptor.class);

    @Override
    public void beforeTask(TaskParam param, TaskContext context) {
        logger.info("before task param:{}", param);
    }

    @Override
    public void afterTask(TaskResult result) {
        logger.info("after task result:{}", result);
    }

    @Override
    public void onSuccess(int id, TaskParam taskParam, TaskContext taskContext, TaskResult taskResult) {
        
    }

    @Override
    public void onFailure(int id, TaskParam taskParam, TaskContext taskContext, TaskResult taskResult) {

    }

}
