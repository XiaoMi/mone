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

package com.xiaomi.youpin.codecheck.code.impl;

import com.sun.source.tree.MethodTree;
import com.xiaomi.youpin.codecheck.po.CheckResult;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author goodjava@qq.com
 */
public abstract class MethodCheck {

    protected static final Pair<Integer, CheckResult> success = Pair.of(CheckResult.INFO, CheckResult.getInfoRes("", "", ""));


    public Pair<Integer, CheckResult> check(MethodTree methodTree) {
        try {
            return _check(methodTree);
        } catch (Throwable throwable) {
            return Pair.of(CheckResult.INFO, null);
        }
    }

    public abstract Pair<Integer, CheckResult> _check(MethodTree methodTree);
}
