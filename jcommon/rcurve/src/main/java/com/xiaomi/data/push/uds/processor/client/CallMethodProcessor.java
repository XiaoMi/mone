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

package com.xiaomi.data.push.uds.processor.client;

import com.google.gson.Gson;
import com.xiaomi.data.push.common.CovertUtils;
import com.xiaomi.data.push.common.Send;
import com.xiaomi.data.push.uds.codes.CodesFactory;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import com.xiaomi.youpin.docean.common.MethodReq;
import com.xiaomi.youpin.docean.common.ReflectUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.function.Function;


/**
 * @author goodjava@qq.com
 * <p>
 * 反射调用方法(从mesh agent 拿到的参数)
 */
@Slf4j
public class CallMethodProcessor implements UdsProcessor {

    private final Function<UdsCommand, Object> beanFactory;

    public CallMethodProcessor(Function<UdsCommand, Object> beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void processRequest(UdsCommand req) {
        UdsCommand response = UdsCommand.createResponse(req);
        try {
            Object obj = beanFactory.apply(req);
            String[] types = req.getParamTypes() == null ? new String[]{} : req.getParamTypes();
            String[] paramArray = req.getParams() == null ? new String[]{} : req.getParams();
            log.info("invoke method : {} {} {} {}", req.getServiceName(), req.getMethodName(), Arrays.toString(types), Arrays.toString(paramArray));

            MethodReq mr = new MethodReq();
            mr.setMethodName(req.getMethodName());
            mr.setParamTypes(types);
            mr.setParams(paramArray);
            mr.setByteParams(req.getByteParams());

            Object res = ReflectUtils.invokeMethod(mr, obj, CovertUtils::convert);

            if (req.getAtt("resultJson", "false").equals("true")) {
                if (res.getClass().equals(int.class) || res.getClass().equals(Integer.class)
                        || res.getClass().equals(long.class)  || res.getClass().equals(Long.class)
                        || res.getClass().equals(String.class)) {
                    response.putAtt("_returnType", res.getClass().getSimpleName());
                    response.setData(CodesFactory.getCodes((byte) 2).encode(res), false);
                } else {
                    response.setData(new Gson().toJson(res).getBytes());
                }
            } else {
                response.setData(res);
            }
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
            response.setCode(500);
            response.setMessage("invoke method error:" + req.getServiceName() + "->" + req.getMethodName() + " error:" + ex.getMessage());
        }
        Send.sendResponse(req.getChannel(), response);
    }

    @Override
    public String cmd() {
        return "call";
    }
}
