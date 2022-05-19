package com.xiaomi.data.push.schedule;


import com.google.common.base.Stopwatch;
import com.xiaomi.data.push.common.PushService;
import com.xiaomi.data.push.common.TaskCache;
import com.xiaomi.data.push.dao.mapper.TaskMapper;
import com.xiaomi.data.push.dao.model.TaskWithBLOBs;
import com.xiaomi.data.push.schedule.task.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * <p>
 * 缓存任务是基于一个假设,就是大部分的任务其实只需要执行一次,而且并不太在乎什么时候保存结果到数据库中
 */
@Service
@Slf4j
public class TaskCacheUpdater implements PushService {


    @Autowired
    private TaskMapper taskMapper;


    private ConcurrentHashMap<Integer, TaskCache> taskCacheMap = new ConcurrentHashMap<>();


    private void updateCacheTask() {
        Stopwatch sw = Stopwatch.createStarted();
        int size = this.taskCacheMap.size();
        List<TaskWithBLOBs> list = taskCacheMap.values().stream().filter(it -> it.getTask().getStatus() != (TaskStatus.Running.code)).map(it -> it.getTask()).collect(Collectors.toList());

        list.stream().forEach(it -> taskMapper.updateByPrimaryKeyWithBLOBs(it));
        list.stream().map(it -> it.getId()).forEach(it -> taskCacheMap.remove(it));

        long now = System.currentTimeMillis();

        //寻找很久都没执行完的任务,清理掉
        List<Integer> ids = taskCacheMap.values().stream().filter(it -> now - it.getTime() >= TimeUnit.MINUTES.toMillis(10))
                .map(it -> it.getTask().getId())
                .collect(Collectors.toList());
        log.warn("updateCacheTask error ids:{}", ids);

        ids.stream().forEach(id -> this.taskCacheMap.remove(id));

        log.info(" list:updateCacheTask{} remove:{} time:{}", size, list.size(), sw.elapsed(TimeUnit.MILLISECONDS));
    }


    public void putTask(TaskWithBLOBs task) {
        TaskCache cache = new TaskCache();
        cache.setTask(task);
        cache.setTime(System.currentTimeMillis());
        this.taskCacheMap.put(task.getId(), cache);
    }


    public void updateTask(int id, String result, String context, int status) {
        log.info("TaskCacheUpdater updateTask: id:{},status:{}", id,status);
        TaskWithBLOBs task = taskCacheMap.get(id).getTask();
        if (null != task) {
            task.setResult(result);
            task.setContext(context);
            task.setStatus(status);
        }
    }


    public void schedule() {
        updateCacheTask();
    }
}
