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

package com.xiaomi.youpin.codecheck.code.impl.flowcontrol;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.StatementTree;
import com.sun.tools.javac.tree.JCTree;
import com.xiaomi.youpin.codecheck.code.impl.MethodCheck;
import com.xiaomi.youpin.codecheck.po.CheckResult;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * @author goodjava@qq.com
 * 当switch括号内的变量类型为String并且此变量为外部参数时，必须先进行null 判断。
 *
 * 有两个问题需要修复
 * 1.语句的位置问题
 * 2.if里边的判断如果是方法
 */
public class SwitchCheck extends MethodCheck {




    @Override
    public Pair<Integer, CheckResult> _check(MethodTree methodTree) {
        BlockTree body = methodTree.getBody();
        //interface 没有方法体
        if (null == body) {
            return success;
        }

        //获取所有
        List<? extends StatementTree> stats = body.getStatements();
        Optional<? extends JCTree.JCSwitch> switchStatOptional = (Optional<? extends JCTree.JCSwitch>) stats.stream().filter(it -> it instanceof JCTree.JCSwitch).findFirst();
        //如果找到了switch语句,则需要校验是否对参数有验空处理
        if (switchStatOptional.isPresent()) {
            JCTree.JCSwitch ss = switchStatOptional.get();

            JCTree.JCExpression ex = ((JCTree.JCParens) ss.getExpression()).getExpression();

            //是方法的就先不check了
            if (ex instanceof JCTree.JCMethodInvocation) {
                return success;
            }



            JCTree.JCIdent ident = (JCTree.JCIdent) ((JCTree.JCParens) ss.getExpression()).getExpression();

            //获取到switch中的变量
            String param = ident.getName().toString();
            //查询上边的所有if 判断
            List<JCTree.JCIf> ifList = (List<JCTree.JCIf>) stats.stream().filter(it -> it instanceof JCTree.JCIf).collect(Collectors.toList());

            //开始寻找是否有if判断
            long count = ifList.stream().filter(it -> {
                JCTree.JCExpression cd = it.getCondition();
                if (cd instanceof JCTree.JCParens) {
                    JCTree.JCParens jp = (JCTree.JCParens) cd;
                    JCTree.JCExpression ex2 = jp.getExpression();
                    if (ex2 instanceof JCTree.JCBinary) {
                        JCTree.JCBinary jb = (JCTree.JCBinary) ex2;
                        String r = jb.getRightOperand().toString();
                        String l = jb.getLeftOperand().toString();
                        //找到了 (左右都可以)
                        if (r.equals(param) || l.equals(param)) {
                            return true;
                        }
                    }
                    return false;
                }
                return false;
            }).count();

            if (count > 0) {
                return success;
            } else {
                return Pair.of(CheckResult.WARN, CheckResult.getWarnRes("", "switch param need check null", ""));
            }
        }

        return success;
    }

}
