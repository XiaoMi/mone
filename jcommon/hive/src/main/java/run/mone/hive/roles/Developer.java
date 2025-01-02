package run.mone.hive.roles;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.schema.Task;
import run.mone.hive.schema.TaskResult;

import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 2025/1/2 15:25
 */
@Slf4j
public class Developer extends Role {


    public Developer(String name, String profile, String goal, String constraints, Consumer<Role> consumer) {
        super(name, profile, goal, constraints, consumer);
    }


    @Override
    public TaskResult executeTask(Task task) {
        String type = task.getTaskType();
        log.info("execute task type:{}", type);
        return TaskResult.builder().content("success").success(true).build();
    }
}
