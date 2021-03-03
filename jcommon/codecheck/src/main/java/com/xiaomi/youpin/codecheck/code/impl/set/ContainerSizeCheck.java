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

package com.xiaomi.youpin.codecheck.code.impl.set;

import com.google.common.collect.Sets;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.tree.JCTree;
import com.xiaomi.youpin.codecheck.code.impl.VariableCheck;
import com.xiaomi.youpin.codecheck.po.CheckResult;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;

/**
 * @author goodjava@qq.com
 * <p>
 * check 容器是否设置了初始值
 */
public class ContainerSizeCheck extends VariableCheck {

    private static final Set<String> set = Sets.newHashSet("ArrayList<>", "HashMap<>", "HashSet<>");


    @Override
    public Pair<Integer, CheckResult> _check(VariableTree tree) {
        if (tree instanceof JCTree.JCVariableDecl) {
            JCTree.JCVariableDecl jc = (JCTree.JCVariableDecl) tree;
            JCTree.JCExpression ex = jc.getInitializer();
            if (ex instanceof JCTree.JCNewClass) {
                JCTree.JCNewClass nc = (JCTree.JCNewClass) ex;
                if (set.contains(nc.getIdentifier().toString())) {
                    if (nc.getArguments().size() == 0) {
                        return Pair.of(CheckResult.WARN, CheckResult.getInfoRes("容器需要制定初始值", tree.toString(), "容器需要制定初始值"));
                    }
                }
            }
        }
        return success;
    }
}
