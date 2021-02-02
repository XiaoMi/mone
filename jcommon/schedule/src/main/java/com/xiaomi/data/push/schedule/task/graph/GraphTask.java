/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
 * 图任务
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
        //获取执行到的步骤
        int step = context.getInt(Step);

        //读取任务的配置
        if (0 == step) {
            //获取任务配置
            String config = param.get(Config);
            context.put(Config, config);
            step = 1;
            context.putInt(Step, step);
        }

        //初始化所有子任务
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

            //初始化
            graph.bfs(0, (v, d) -> {
                int id = taskService.insertTask(taskId, new Gson().toJson(d.getTaskParam()), new Gson().toJson(new TaskContext()), d.getTaskDef().getName(), TaskStatus.Init, micheduleGroup);
                //最后会保存下来
                d.setTaskId(id);
                //依赖的任务
                d.setDependList(graph.dependList(d.getIndex()));
                return true;
            });

            //保存新的
            context.put(Config, new Gson().toJson(taskContext));
            context.putInt(Step, 2);
            return TaskResult.Retry();
        }

        if (2 == step) {
            GraphTaskContext taskContext = new Gson().fromJson(context.get(Config), GraphTaskContext.class);
            Graph<TaskVertexData> graph = new Graph<>(taskContext.getTaskList().size());

            //添加顶点
            taskContext.getTaskList().stream().forEach(it -> {
                TaskVertexData data = (TaskVertexData) it;
                graph.addVertex(new Vertex(data.getIndex(), data));
            });

            //添加边
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

                //初始化状态
                if (d.getStatus() == TaskStatus.Init.code) {
                    List<Integer> list = d.getDependList();
                    //没有依赖
                    if (list.size() == 0) {
                        startTask(d, graph);
                    } else {
                        boolean match = list.stream().allMatch(it -> {
                            if (graph.getVertexData(it).getStatus() == TaskStatus.Success.code) {
                                return true;
                            }
                            return false;
                        });
                        //依赖的所有任务都执行成功了
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

            //保存新的
            context.put(Config, new Gson().toJson(taskContext));
            //有任务失败了
            if (failureNum.getValue() >= 1) {
                //任务失败
                return TaskResult.Failure();
            }
            //所有任务都已经完成
            if (finishNum.getValue() == graph.V) {
                //任务完成
                return TaskResult.Success();
            }
        }


        return TaskResult.Retry();
    }

    /**
     * 启动任务+参数替换
     *
     * @param d
     * @param graph
     */
    private void startTask(TaskVertexData d, Graph<TaskVertexData> graph) {
        d.setStatus(TaskStatus.Retry.code);

        TaskParam param = d.getTaskParam();

        MutableBoolean changeParam = new MutableBoolean(false);

        //$开头的参数会被动态替换掉
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
            //参数发生了替换
            if (changeParam.getValue()) {
                it.setParams(new Gson().toJson(param));
            }
            it.setStatus(TaskStatus.Retry.code);
            return true;
        });

    }
}
