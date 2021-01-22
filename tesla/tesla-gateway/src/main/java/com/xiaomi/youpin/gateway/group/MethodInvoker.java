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

package com.xiaomi.youpin.gateway.group;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.youpin.gateway.common.Msg;
import com.xiaomi.youpin.gateway.group.filter.GateWayFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * @author goodjava@qq.com
 */
public class MethodInvoker {

    private static final Logger logger = LoggerFactory.getLogger(MethodInvoker.class);


    @Autowired
    private ApplicationContext ac;


    private ArrayBlockingQueue queue = new ArrayBlockingQueue<>(5000);

    private ArrayBlockingQueue timeLimiterQueue = new ArrayBlockingQueue<>(5000);


    private ConcurrentHashMap<String, Method> methodCache = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, MethodAccess> methodAccessCache = new ConcurrentHashMap<>();

    private SimpleTimeLimiter timeLimiter = null;


    private ThreadPoolExecutor pool = null;

    private ListeningExecutorService listeningExecutor = null;

    @PostConstruct
    public void init() {
        timeLimiter = SimpleTimeLimiter.create(new ThreadPoolExecutor(100, 100, 0L, TimeUnit.MILLISECONDS, timeLimiterQueue, new NamedThreadFactory("MethodInvoker"), (r, executor) -> {
            logger.warn("SimpleTimeLimiter invoke rejected");
        }));

        pool = new ThreadPoolExecutor(100, 100, 0L, TimeUnit.MILLISECONDS, queue, new NamedThreadFactory("MethodInvoker"), (r, executor) -> {
            logger.warn("MethodInvoker invoke rejected");
        });

        listeningExecutor = MoreExecutors.listeningDecorator(pool);

    }

    public MethodInvoker() {
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
            try {
                logger.info("MethodInvoker completed count:{} active count:{} pool size:{} timeLimiter queue size:{}", pool.getCompletedTaskCount(), pool.getActiveCount(), queue.size(), timeLimiterQueue.size());
            } catch (Exception ex) {
                //ignore
            }

        }, 0, 5, TimeUnit.SECONDS);
    }

    /**
     * group 如果有next group 也会执行下边的group
     *
     * @param request
     * @return
     */
    public Map<String, ApiResult> invokeGroup(ApiRequest request, Map<String, ApiResult> r) {
        String groupName = request.getGroupName();
        List<MethodInfo> list = Lists.newArrayList();//TODO ---

        List<String[]> paramList = request.getGroupParams();
        List<MethodInfo> methods = IntStream.range(0, list.size()).mapToObj(i -> {
            MethodInfo m = list.get(i);
            String[] params = paramList.get(i);
            m.setParams(params);
            return m;
        }).collect(Collectors.toList());
        request.setMethods(methods);
        GroupInfo groupInfo = null;//TODO ---

        Map<String, ApiResult> res = invoke(request);

        r.putAll(res);

        GateWayFilter groupFilter = groupInfo.getFilter();

        //执行group级别的filter
        if (null != groupFilter) {
            groupFilter.invoke(r, request);
        }

        if (StringUtils.isNotEmpty(groupInfo.getNextGroup())) {
            //递归调用
            request.setGroupName(groupInfo.getNextGroup());
            return invokeGroup(request, r);
        }
        return res;
    }


    class ApiCallable implements Callable<ApiResult> {

        private MethodInfo methodInfo;

        private ApiRequest request;


        public ApiCallable(String name, MethodInfo it, ApiRequest request) {
            this.methodInfo = it;
            this.request = request;
        }


        @Override
        public ApiResult call() throws Exception {
            MethodInfo methodInfo = this.methodInfo;
            //如果提供别名了,则按别名获取完整的方法调用信息
            if (StringUtils.isNotEmpty(this.methodInfo.getCmd())) {
                String cmd = this.methodInfo.getCmd();
                methodInfo = null;
                methodInfo.setParams(this.methodInfo.getParams());
            }

            Object service = ac.getBean(methodInfo.getServiceName());
            ApiResult result = new ApiResult();
            result.setName(this.methodInfo.getMethodName());
            try {

                String[] params = methodInfo.getParams();
                String[] paramTypes = methodInfo.getParamTypes();

                String methodKey = methodInfo.getMethodName() + "_" + Arrays.stream(paramTypes).collect(Collectors.joining(","));

                Method method = null;

                Class[] classArray = Arrays.stream(paramTypes).map(it2 -> {
                    return null;
                }).toArray(Class[]::new);


                if (true) {
                    method = methodCache.get(methodKey);
                    if (null == method) {
                        method = service.getClass().getMethod(methodInfo.getMethodName(), classArray);
                        methodCache.putIfAbsent(methodKey, method);
                    }
                } else {
                    method = service.getClass().getMethod(methodInfo.getMethodName(), classArray);
                }

                Gson gson = new Gson();
                final Type[] types = method.getGenericParameterTypes();
                Object[] objs = IntStream.range(0, params.length).mapToObj(i -> {

                    String json = params[i];
                    if (classArray[i].getName().equals("java.lang.String")) {
                        return json;
                    }
                    if (classArray[i].getName().equals("long")) {
                        return Long.valueOf(json);
                    }
                    Object obj = getObj(gson, json, i, types);
                    return obj;
                }).toArray();


                Object res = null;
                if (true) {
                    MethodAccess methodAccess = methodAccessCache.get(methodKey);
                    if (null == methodAccess) {
                        methodAccess = MethodAccess.get(service.getClass());
                        methodAccessCache.putIfAbsent(methodKey, methodAccess);
                    }
                    res = methodAccess.invoke(service, this.methodInfo.getMethodName(), objs);
                } else {
                    res = method.invoke(service, objs);
                }


                //执行方法级别的filter
                if (this.methodInfo.getFilter() != null) {
                    res = this.methodInfo.getFilter().invoke(res, request);
                }

                result.setResult(res);
            } catch (Throwable e) {
                logger.warn("invoke " + result.getName() + " error:" + e.getMessage());
                result.setMessage(Msg.msgFor500);
                result.setCode(500);
            }
            return result;
        }
    }

    private Object getObj(Gson gson, String value, int index, Type[] types) {
        return gson.fromJson(value, TypeToken.get(types[index]).getType());
    }


    /**
     * 只执行当下的methods
     *
     * @param request
     * @return
     */
    public Map<String, ApiResult> invoke(ApiRequest request) {

        List<MethodInfo> methods = request.getMethods();

        List<ApiCallable> tasks = methods.stream().map(it -> new ApiCallable(it.getMethodName(), it, request)).collect(Collectors.toList());

        CountDownLatch latch = new CountDownLatch(tasks.size());

        List<ListenableFuture<ApiResult>> futureList = tasks.stream().map(it -> {
            ListenableFuture<ApiResult> f = listeningExecutor.submit(() -> {
                ApiResult ar = new ApiResult();
                try {
                    long timeOut = 800L;
                    ar = MethodInvoker.this.timeLimiter.callWithTimeout(it, timeOut, TimeUnit.MILLISECONDS);
                } catch (Throwable e) {
                    ar.setName(it.methodInfo.getMethodName());
                    ar.setMessage(Msg.msgFor500);
                    ar.setCode(500);
                    logger.warn("method {} invoker error:{}", ar.getName(), e.getMessage());
                }
                return ar;
            });
            return f;
        }).collect(Collectors.toList());

        futureList.stream().forEach(future -> {
            Futures.addCallback(future, new FutureCallback<ApiResult>() {
                @Override
                public void onSuccess(ApiResult result) {
                    latch.countDown();
                }

                @Override
                public void onFailure(Throwable t) {
                    latch.countDown();
                }
            }, MoreExecutors.directExecutor());
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<ApiResult> list2 = futureList.stream().map(it -> {
            ApiResult perRes = null;
            try {
                perRes = it.get(1000, TimeUnit.MILLISECONDS);
            } catch (Throwable e) {
                logger.warn("method get res error:{}", e.getMessage());
            }
            return perRes;
        }).collect(Collectors.toList());
        Map<String, ApiResult> m = list2.stream().collect(Collectors.toMap(it -> it.getName(), it -> it));
        return m;
    }

}
