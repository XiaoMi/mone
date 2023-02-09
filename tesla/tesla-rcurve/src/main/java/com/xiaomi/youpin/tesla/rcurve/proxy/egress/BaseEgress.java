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

package com.xiaomi.youpin.tesla.rcurve.proxy.egress;

import com.alibaba.nacos.api.exception.NacosException;
import com.xiaomi.data.push.common.CovertUtils;
import com.xiaomi.data.push.common.Send;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.common.MethodReq;
import com.xiaomi.youpin.docean.common.ReflectUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/13 17:11
 */
@Slf4j
public abstract class BaseEgress implements UdsProcessor {


    @Override
    public void processRequest(UdsCommand req) {
        //使用app 维度隔离
        String app = req.getApp();
        //这个是动态注入进来的(mysql redis nacos....)
        Object obj = Ioc.ins().getBean(getBeanName(app));
        //调用的方法需要保证是同构的
        try {
            MethodReq mr = new MethodReq();
            mr.setMethodName(req.getMethodName());
            mr.setParamTypes(req.getParamTypes());
            mr.setParams(req.getParams());
            mr.setByteParams(req.getByteParams());
            mr.setSerializeType(req.getSerializeType());
            Object rr = ReflectUtils.invokeMethod(mr, obj, (classes, bytes) -> CovertUtils.convert(req.getSerializeType(), classes, bytes));
            log.info("egress invoke result:{}", rr);
            UdsCommand res = UdsCommand.createResponse(req);
            res.setData(rr);
            Send.sendResponse(req.getChannel(), res);
        } catch (Throwable ex) {
            String message = ex.getMessage();
            if (ex instanceof NacosException) {
                message = ((NacosException) ex).getErrMsg();
            }
            message = "entry error:" + getBeanName(app) + ":" + message;
            log.error("{}", message);
            Send.sendMessage(req.getChannel(), message);
            UdsCommand res = UdsCommand.createErrorResponse(req.getId(), message);
            Send.sendResponse(req.getChannel(), res);
        }
    }


    public abstract String getBeanName(String app);
}
