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

package com.xiaomi.youpin.tesla.rcurve.proxy.control.flow;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.tesla.rcurve.proxy.control.ControlCallable;
import com.xiaomi.youpin.tesla.rcurve.proxy.control.IControl;
import com.xiaomi.youpin.tesla.rcurve.proxy.control.Invoker;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/14 18:20
 * 限流控制
 */
@Component(order = 200)
public class FlowControl implements IControl {

    /**
     * 初始化规则
     */
    public void init() {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        rule.setResource("R1");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(1);
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }


    @Override
    public Object call(ControlCallable call, Invoker invoker) {
        String resource = call.getResource();
        if (needControl(resource)) {
            try (Entry entry = SphU.entry(call.getResource())) {
                return invoker.invoke(call);
            } catch (BlockException e1) {
                throw new RuntimeException(e1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return invoker.invoke(call);
    }

    private boolean needControl(String resource) {
        if (true) {
            return true;
        }
        return FlowRuleManager.hasConfig(resource);
    }
}
