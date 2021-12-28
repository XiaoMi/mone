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

package com.xiaomi.youpin.gateway.netty.filter.request;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.data.push.antlr.expr.Expr;
import com.xiaomi.data.push.antlr.json.Json;
import com.xiaomi.data.push.graph.Graph;
import com.xiaomi.data.push.graph.Vertex;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.graph.GraphTaskContext;
import com.xiaomi.data.push.schedule.task.graph.TaskVertexData;
import com.xiaomi.youpin.gateway.RouteType;
import com.xiaomi.youpin.gateway.bo.CodeResponse;
import com.xiaomi.youpin.gateway.cache.ApiRouteCache;
import com.xiaomi.youpin.gateway.common.*;
import com.xiaomi.youpin.gateway.dispatch.Dispatcher;
import com.xiaomi.youpin.gateway.exception.GatewayException;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.Invoker;
import com.xiaomi.youpin.gateway.filter.RequestContext;
import com.xiaomi.youpin.gateway.filter.RequestFilter;
import com.xiaomi.youpin.gateway.netty.Body;
import com.xiaomi.youpin.gateway.netty.Header;
import com.xiaomi.youpin.gateway.netty.Param;
import com.xiaomi.youpin.gateway.netty.Request;
import com.xiaomi.youpin.gateway.netty.filter.RequestFilterChain;
import com.xiaomi.youpin.gateway.task.TaskStatus;
import com.xiaomi.youpin.infra.rpc.Result;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.youpin.xiaomi.tesla.bo.GroupConfig;
import com.youpin.xiaomi.tesla.bo.NodeInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.mutable.MutableInt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * <p>
 * 这里其实会调用dag
 * 解析图依赖
 * <p>
 * 单个任务是列表任务的特列
 * 列表任务是图任务的特例
 *
 * <p>
 * 可以执行组任务的返回结果必须是json
 * dubbo会自动转换
 * http的必须是json.不然不支持
 */
@Slf4j
@Component
@FilterOrder(3999)
public class GroupFilter extends RequestFilter {

    @Autowired
    private ApiRouteCache apiRouteCache;

    @Autowired
    private RequestFilterChain chain;

    @Autowired
    private Dispatcher dispatcher;


    @Autowired
    private Redis redis;

    @Override
    public FullHttpResponse doFilter(FilterContext ctx, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {

        if (apiInfo.getRouteType() != RouteType.Group.type()) {
            return invoker.doInvoker(ctx, apiInfo, request);
        }

        ctx.setGroupConfig(new GroupConfig(redis.get(Keys.groupKey(apiInfo.getId()))));


        Request _httpRequest = null;

        //是分组任务
        try {
            Header header = new Header(request);
            Param param = new Param(request);
            Body body = new Body(request);
            _httpRequest = new Request(header, param, body);
        } catch (Throwable ex) {
            log.warn("request error:{}", ex.getMessage());
            return HttpResponseUtils.create(Result.fromException(ex));
        }


        Request httpRequest = _httpRequest;

        //如果是分组任务(Dag 图任务)

        Type type = new TypeToken<GraphTaskContext<NodeInfo>>() {
        }.getType();


        GraphTaskContext<NodeInfo> taskContext = new Gson().fromJson(ctx.getGroupConfig().getConfig(), type);
        Graph<TaskVertexData<NodeInfo>> graph = new Graph<>(taskContext.getTaskList().size());

        //添加顶点
        taskContext.getTaskList().stream().forEach(it -> {
            TaskParam _param = new TaskParam();
            it.setTaskParam(_param);
            graph.addVertex(new Vertex(it.getIndex(), it));
        });

        //添加边
        taskContext.getDependList().stream().forEach(it -> {
            graph.addEdge(it.getFrom(), it.getTo());
        });


        //初始化
        graph.bfsAll((v, d) -> {
            //获取路由id
            int id = d.getData().getId();
            d.setTaskId(id);
            //依赖的任务
            d.setDependList(graph.dependList(d.getIndex()));
            d.setStatus(TaskStatus.Init.code);
            return true;
        });

        //允许重新遍历
        graph.bfsReset();

        List<Integer> sortList = graph.topologicalSort();

        while (true) {
            MutableInt successNum = new MutableInt(0);
            MutableInt failureNum = new MutableInt(0);

            List<TaskVertexData<NodeInfo>> dataList = Lists.newArrayList();

            for (int index : sortList) {
                TaskVertexData d = graph.getVertexData(index);

                if (d.getStatus() == TaskStatus.Failure.code) {
                    failureNum.add(1);
                    break;
                }

                if (d.getStatus() == TaskStatus.Success.code) {
                    successNum.add(1);
                    continue;
                }

                //初始化状态
                if (d.getStatus() == TaskStatus.Init.code) {
                    List<Integer> list = d.getDependList();
                    //没有依赖
                    if (list.size() == 0) {
                        d.setStatus(TaskStatus.Running.code);
                        dataList.add(d);
                    } else if (allSuccess(graph, list)) {
                        //依赖的所有任务都执行成功了
                        d.setStatus(TaskStatus.Running.code);
                        dataList.add(d);
                    } else {
                        //不成功,后边肯定都不成功
                        break;
                    }
                }

            }

            if (failureNum.getValue() > 0) {
                log.warn("failureNum > 0");
                break;
            }

            if (successNum.getValue() == graph.V) {
                break;
            }

            if (dataList.size() > 0) {
                CountDownLatch latch = new CountDownLatch(dataList.size());
                RequestContext context = new RequestContext();
                //用来获取bytebuf pool
                context.setChannel(ctx.getRequestContext().getChannel());
                context.setLatch(latch);
                context.setCallId(ctx.getCallId() + "_group_" + TraceId.uuid());
                dataList.stream().forEach(it -> startTask(context, httpRequest, it, graph, request.headers(), request.trailingHeaders()));
                try {
                    //这里不用担心,会被外边打断的
                    latch.await(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    log.warn("latch wait ex:{}", e.getMessage());
                }
            } else {
                //容错处理
                break;
            }


        }


        Gson gson = Utils.getGson();
        //打成列表(拿去图中的结果列表)
        List<Object> list = graph.getVertexMap().values()
                .stream()
                .filter(it -> it.getData().isResult())
                //转化结果
                .map(it -> {
                    if (it.getTaskResult() == null) {
                        return new CodeResponse(500);
                    }
                    return gson.fromJson(it.getTaskResult().toString(), Object.class);
                }).collect(Collectors.toList());
        //返回结果
        return HttpResponseUtils.create(gson.toJson(list));

    }

    private boolean allSuccess(Graph<TaskVertexData<NodeInfo>> graph, List<Integer> list) {
        return list.stream().allMatch(it -> {
            if (graph.getVertexData(it).getStatus() == TaskStatus.Success.code) {
                return true;
            }
            return false;
        });
    }


    /**
     * request_0_params.header{name}
     * request_0_params.param{name}
     * request_0_params.body{name}
     * <p>
     * 结果需要先转换成map
     * response_1_result.toMap(){name}
     */
    @Data
    class ExprValue {
        /**
         * request
         * response
         */
        private String cmd;
        /**
         * 索引(只有一个值,填0)
         */
        private int index;
        /**
         * 取值表达式
         */
        private String expr;

        public ExprValue(String value) {
            String[] ss = value.split("_");
            cmd = ss[0];
            index = Integer.valueOf(ss[1]);
            expr = ss[2];
        }
    }


    enum CmdType {
        request,
        response,
    }


    /**
     * 启动任务+参数替换
     * <p>
     * 替换参数只有两个途径能拿到:传进来的参数+之前执行完的任务的结果
     * <p>
     * 从header中取值
     * request_0_.header{name}
     * 从get的url参数中取值
     * request_0_.param{name}
     * 从post的body中取值
     * request_0_.body{name}
     * <p>
     * 从第三个节点的返回结果中取值
     * response.3.name
     *
     * @param d
     * @param graph
     */
    private void startTask(RequestContext context, Request request, TaskVertexData d, Graph<TaskVertexData<NodeInfo>> graph, HttpHeaders headers, HttpHeaders trailingHeaders) {

        //node 信息
        NodeInfo nodeInfo = (NodeInfo) d.getData();

        d.setStatus(TaskStatus.Retry.code);

        //提取值
        nodeInfo.getParamExtract().entrySet().stream().forEach(it -> {
            //取的值的名称
            String key = it.getKey();
            //取值表达式
            String value = it.getValue();

            ExprValue ev = new ExprValue(value);

            Object v = null;
            //从request中提取
            //example:
            //params.header{name}
            //params.param{name}
            //params.body{name}
            if (ev.getCmd().equals(CmdType.request.name())) {
                v = Expr.params(request, ev.getExpr());
            } else if (ev.getCmd().equals(CmdType.response.name())) {
                //从response中取值
                //response.1
                //获取保存好的result
                TaskVertexData data = graph.getVertexMap().get(ev.getIndex());
                Object res = data.getTaskResult();
                //expr=result.toMap(){name}
                v = Expr.result(res, ev.getExpr());
            }
            //放入参数列表
            nodeInfo.getParamMap().put(key, new Gson().toJson(v));
        });


        //找到下游节点的apiInfo
        //这里要改成并行化
        ApiInfo apiInfo = apiRouteCache.get(nodeInfo.getUrl());


        HttpMethod method = null;
        String url = "";
        ByteBuf buf = null;
        //如果是http
        if (apiInfo.getRouteType().equals(RouteType.Http.type())) {
            if (apiInfo.getHttpMethod().toUpperCase().equals(HttpMethod.GET.name())) {
                method = HttpMethod.GET;
                String queryString = nodeInfo.getParamMap().entrySet().stream().map(it -> it.getKey() + "=" + it.getValue()
                ).collect(Collectors.joining("&"));
                url = apiInfo.getPath() + "?" + queryString;
            } else if (apiInfo.getHttpMethod().toUpperCase().equals(HttpMethod.POST.name())) {
                method = HttpMethod.POST;
                Map<String, String> paramMap = nodeInfo.getParamMap();
                String body = nodeInfo.getBody();
                body = Json.json(body, paramMap);
                String param = body;
                if (headers.get("Content-Type") != null && headers.get("Content-Type").contains("application/json")
                        && body != null && body.startsWith("\"{") && body.endsWith("}\"")) {
                    param = body.substring(1, body.length() - 1);
                }
                buf = Unpooled.wrappedBuffer(param.getBytes());
            }
        } else if (apiInfo.getRouteType().equals(RouteType.Dubbo.type())) {
            //post 其实替换的body
            Map<String, String> paramMap = nodeInfo.getParamMap();
            method = HttpMethod.POST;
            url = apiInfo.getPath();
            String body = nodeInfo.getBody();
            //替换body中的变量
            body = Json.json(body, paramMap);
            buf = Unpooled.wrappedBuffer(body.getBytes());
        }


        if (buf == null) {
            buf = Unpooled.EMPTY_BUFFER;
        }

        final FullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method, url, buf, headers, trailingHeaders);

        dispatcher.dispatcher((str) -> {
            FullHttpResponse fhres = null;
            try {
                context.setIp("");
                if (null == context.getCallId()) {
                    log.debug("call id is null");
                    context.setCallId("");
                }
                fhres = chain.doFilter(apiInfo, req, context);
                //放入结果的string信息
                //结果都要符合json的样式,不然不能执行group任务
                d.setTaskResult(HttpResponseUtils.getContent(fhres));
                d.setStatus(TaskStatus.Success.code);
                context.getLatch().countDown();
                return "";
            } catch (Throwable ex) {
                log.error("GroupFilter ex:{}", ex.getMessage());
                throw new GatewayException(ex);
            } finally {
                if (null != fhres) {
                    NettyUtils.release(fhres.content(), "groupFilter_release_response");
                }
            }
        }, (res) -> TeslaSafeRun.run(() -> NettyUtils.release(request, "groupFilter_release_request")), null);


    }


}
