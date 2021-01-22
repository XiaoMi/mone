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

package com.xiaomi.youpin.tesla.rcurve.proxy.control.degrade;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.tesla.rcurve.proxy.control.ControlCallable;
import com.xiaomi.youpin.tesla.rcurve.proxy.control.IControl;
import com.xiaomi.youpin.tesla.rcurve.proxy.control.Invoker;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/14 21:44
 * 熔断控制
 */
@Component(order = 100)
@Slf4j
public class DegradeControl implements IControl {

    public void init() {
        List<DegradeRule> rules = new ArrayList<>();
        DegradeRule rule = new DegradeRule();
        rule.setResource("D1");
        rule.setCount(10);
        rule.setGrade(RuleConstant.DEGRADE_GRADE_RT);
        rule.setTimeWindow(30);
        rule.setMinRequestAmount(1);
        rules.add(rule);
        DegradeRuleManager.loadRules(rules);
    }

    @Override
    public Object call(ControlCallable call, Invoker invoker) {
        if (DegradeRuleManager.hasConfig(call.getResource())) {
            try (Entry entry = SphU.entry(call.getResource())) {
                return invoker.invoke(call);
            } catch (BlockException e) {
                log.error("degrade:" + e.getMessage());
                throw new RuntimeException(e);
            } catch (Throwable ex) {
                log.error(ex.getMessage());
            }
        }
        return invoker.invoke(call);
    }

}
