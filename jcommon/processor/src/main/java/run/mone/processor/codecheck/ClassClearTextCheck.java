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

package run.mone.processor.codecheck;

import com.google.common.collect.Sets;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.tree.JCTree;
import run.mone.processor.common.Pair;
import run.mone.processor.common.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * @author goodjava@qq.com
 * 检查用户代码中是否有设置明文password的操作
 * 会check每个方法
 */
public class ClassClearTextCheck {

    private String className;

    private String message = " 有设置明文的操作(请放入nacos),明文是:";

    private Set<String> methodNames = Sets.newHashSet("password", "setPwd", "setIp", "setHost", "ip", "host");

    public Pair<Integer, CheckResult> _check(ClassTree classTree, Consumer<String> consumer) {
        className = classTree.getSimpleName().toString();
        List<? extends Tree> members = classTree.getMembers();
        Map<String, String> valueMap = members.stream().filter(it -> {
            if (it instanceof JCTree.JCVariableDecl) {
                return true;
            }
            return false;
        }).map(it -> {
            JCTree.JCVariableDecl var = (JCTree.JCVariableDecl) it;
            Value value = new Value();
            value.setName(var.getName().toString());
            if (null == var.getInitializer()) {
                value.setValue("");
            } else {
                value.setValue(var.getInitializer().toString().replaceAll("\"", ""));
            }
            return value;
        }).collect(Collectors.toMap(Value::getName, Value::getValue));

        members.forEach(it -> {
            if (it instanceof JCTree.JCMethodDecl) {
                JCTree.JCMethodDecl method = (JCTree.JCMethodDecl) it;
                checkPassword(method, valueMap, consumer);
            }
        });
        return Pair.of(CheckResult.INFO, CheckResult.getInfoRes("class name: " + className, "", ""));
    }


    public void checkPassword(JCTree.JCMethodDecl methodDecl, Map<String, String> valueMap, Consumer<String> consumer) {
        //获取所有
        BlockTree body = methodDecl.getBody();
        List<? extends StatementTree> stats = body.getStatements();

        stats.forEach(it -> {
            if (it instanceof JCTree.JCVariableDecl) {
                JCTree.JCVariableDecl var = (JCTree.JCVariableDecl) it;
                Value value = new Value();
                value.setName(var.getName().toString());
                if (null == var.getInitializer()) {
                    value.setValue("");
                } else {
                    value.setValue(var.getInitializer().toString().replaceAll("\"", ""));
                }
                valueMap.put(value.getName(), value.getValue());
            }
            if (it instanceof JCTree.JCExpressionStatement) {
                JCTree.JCExpressionStatement statement = (JCTree.JCExpressionStatement) it;
                JCTree.JCExpression expression = statement.getExpression();
                if (expression instanceof JCTree.JCMethodInvocation) {
                    JCTree.JCMethodInvocation invocation = (JCTree.JCMethodInvocation) expression;
                    String meth = invocation.getMethodSelect().toString();
                    if (needCheckMethod(meth.toLowerCase())) {
                        com.sun.tools.javac.util.List<JCTree.JCExpression> arguments = invocation.getArguments();
                        arguments.forEach(arg -> {
                            if (arg instanceof JCTree.JCLiteral) {
                                JCTree.JCLiteral literal = (JCTree.JCLiteral) arg;
                                String value = literal.value.toString();
                                if (StringUtils.isNotEmpty(value)) {
                                    consumer.accept(className + message + value);
                                }
                            }
                            if (arg instanceof JCTree.JCIdent) {
                                JCTree.JCIdent ident = (JCTree.JCIdent) arg;
                                String name = ident.getName().toString();
                                if (valueMap.containsKey(name)) {
                                    String value = valueMap.get(name);
                                    if (StringUtils.isNotEmpty(value)) {
                                        consumer.accept(className + message + value);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private boolean needCheckMethod(String name) {
        String str = name.toLowerCase();
        return this.methodNames.stream().filter(it -> str.contains(it)).findAny().isPresent();
    }


}
