package com.xiaomi.data.push.schedule.task.graph;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.data.push.antlr.expr.Expr;
import com.xiaomi.data.push.common.ClassUtils;
import com.xiaomi.data.push.dao.model.TaskWithBLOBs;
import com.xiaomi.data.push.graph.Graph;
import com.xiaomi.data.push.graph.Vertex;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskResult;
import com.xiaomi.data.push.schedule.task.TaskStatus;
import com.xiaomi.data.push.schedule.task.impl.AbstractTask;
import com.xiaomi.data.push.service.TaskService;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

/**
 * Created by zhangzhiyong on 08/06/2018.
 * <p>
 * Graph task
 */
@Component
public class GraphTask extends AbstractTask {

    @Autowired
    private TaskService taskService;

    private static final String Config = "task_config";

    private static final String Step = "step";

    @Value("${michedule_group}")
    private int micheduleGroup;

    @Override
    public TaskResult execute(TaskParam param, TaskContext context) {
        //Gets the steps performed
        int step = context.getInt(Step);

        //Read the configuration of the task
        if (0 == step) {
            //Read the configuration of the task
            String config = param.get(Config);
            context.put(Config, config);
            step = 1;
            context.putInt(Step, step);
        }

        //Initialize all subtasks
        if (1 == step) {
            GraphTaskContext taskContext = new Gson().fromJson(context.get(Config), new TypeToken<GraphTaskContext<Object>>() {
            }.getType());
            Graph<TaskVertexData> graph = new Graph<>(taskContext.getTaskList().size());
            taskContext.getTaskList().stream().forEach(it -> {
                TaskVertexData data = (TaskVertexData) it;
                graph.addVertex(new Vertex(data.getIndex(), data));
            });

            taskContext.getDependList().stream().forEach(it -> {
                TaskEdgeData data = (TaskEdgeData) it;
                graph.addEdge(data.getFrom(), data.getTo());
            });

            int taskId = param.getTaskId();

            //initialized
            graph.bfs(0, (v, d) -> {
                int id = taskService.insertTask(taskId, new Gson().toJson(d.getTaskParam()), new Gson().toJson(new TaskContext()), d.getTaskDef().getName(), TaskStatus.Init, micheduleGroup);
                //It will be preserved in the end
                d.setTaskId(id);
                //Dependent task
                d.setDependList(graph.dependList(d.getIndex()));
                return true;
            });

            //Save new
            context.put(Config, new Gson().toJson(taskContext));
            context.putInt(Step, 2);
            return TaskResult.Retry();
        }

        if (2 == step) {
            GraphTaskContext taskContext = new Gson().fromJson(context.get(Config), GraphTaskContext.class);
            Graph<TaskVertexData> graph = new Graph<>(taskContext.getTaskList().size());

            //Add vertex
            taskContext.getTaskList().stream().forEach(it -> {
                TaskVertexData data = (TaskVertexData) it;
                graph.addVertex(new Vertex(data.getIndex(), data));
            });

            //Add edge
            taskContext.getDependList().stream().forEach(it -> {
                TaskEdgeData data = (TaskEdgeData) it;
                graph.addEdge(data.getFrom(), data.getTo());
            });


            MutableInt finishNum = new MutableInt(0);
            MutableInt failureNum = new MutableInt(0);
            graph.bfs(0, (v, d) -> {

                if (d.getStatus() == TaskStatus.Failure.code) {
                    failureNum.add(1);
                    return false;
                }


                if (d.getStatus() == TaskStatus.Retry.code) {
                    int taskId = d.getTaskId();
                    TaskWithBLOBs task = taskService.findTask(taskId);
                    if (task.getStatus().equals(TaskStatus.Success.code)) {
                        d.setStatus(TaskStatus.Success.code);
                    }
                }

                //Initialization state
                if (d.getStatus() == TaskStatus.Init.code) {
                    List<Integer> list = d.getDependList();
                    //No dependence
                    if (list.size() == 0) {
                        startTask(d, graph);
                    } else {
                        boolean match = list.stream().allMatch(it -> {
                            if (graph.getVertexData(it).getStatus() == TaskStatus.Success.code) {
                                return true;
                            }
                            return false;
                        });
                        //All the dependent tasks were successfully executed
                        if (match) {
                            startTask(d, graph);
                        }
                    }
                }
                if (d.getStatus() == TaskStatus.Success.code) {
                    finishNum.add(1);
                }
                return true;

            });

            //Save new
            context.put(Config, new Gson().toJson(taskContext));
            //A mission has failed
            if (failureNum.getValue() >= 1) {
                //task fail
                return TaskResult.Failure();
            }
            //All tasks have been completed
            if (finishNum.getValue() == graph.V) {
                //completion of task
                return TaskResult.Success();
            }
        }


        return TaskResult.Retry();
    }

    /**
     * Start task + parameter replacement
     *
     * @param d
     * @param graph
     */
    private void startTask(TaskVertexData d, Graph<TaskVertexData> graph) {
        d.setStatus(TaskStatus.Retry.code);

        TaskParam param = d.getTaskParam();

        MutableBoolean changeParam = new MutableBoolean(false);

        //$The starting argument is replaced dynamically
        param.param.entrySet().stream().forEach(it -> {
            if (it.getValue().startsWith("$_")) {
                String[] ss = it.getValue().split("_");
                int taskIndex = Integer.parseInt(ss[1]);
                String className = ss[2];
                String script = ss[3];
                TaskVertexData depData = graph.getVertexData(taskIndex);
                int taskId = depData.getTaskId();
                String result = taskService.findTask(taskId).getResult();
                TaskResult tr = new Gson().fromJson(result, TaskResult.class);
                String data = tr.getData();
                Object m = new Gson().fromJson(data, ClassUtils.classForName(className));
                String v = Expr.result(m, script);
                param.put(it.getKey(), v);
                changeParam.setValue(true);
            }
        });

        taskService.updateTask(d.getTaskId(), (it) -> {
            //The parameter has been replaced
            if (changeParam.getValue()) {
                it.setParams(new Gson().toJson(param));
            }
            it.setStatus(TaskStatus.Retry.code);
            return true;
        });

    }
}
