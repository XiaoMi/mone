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

package com.xiaomi.youpin.codecheck.visitor;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;
import com.xiaomi.youpin.codecheck.code.impl.ClassCheck;
import com.xiaomi.youpin.codecheck.code.impl.CompilationCheck;
import com.xiaomi.youpin.codecheck.code.impl.MethodCheck;
import com.xiaomi.youpin.codecheck.code.impl.VariableCheck;
import com.xiaomi.youpin.codecheck.po.CheckResult;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author goodjava@qq.com
 */
public class SourceVisitor extends TreeScanner<Void, Void> {

    private List<MethodCheck> methodChecks;

    private List<ClassCheck> classChecks;

    private List<VariableCheck> variableChecks;

    private List<CompilationCheck> compilationChecks;

    private List<CheckResult> checkResults = new ArrayList<>();


    public SourceVisitor(List<MethodCheck> methodChecks, List<ClassCheck> classChecks, List<VariableCheck> variableChecks, List<CompilationCheck> compilationChecks) {
        this.methodChecks = methodChecks;
        this.classChecks = classChecks;
        this.variableChecks = variableChecks;
        this.compilationChecks = compilationChecks;
    }

    @Override
    public Void visitMethod(MethodTree node, Void aVoid) {
        methodChecks.stream().forEach(it -> {
            Pair<Integer, CheckResult> res = it.check(node);
            if (res.getKey() > CheckResult.INFO) {
                checkResults.add(res.getRight());
            }
        });
        return super.visitMethod(node, aVoid);
    }


    @Override
    public Void visitClass(ClassTree node, Void aVoid) {
        classChecks.stream().forEach(it -> {
            Pair<Integer, CheckResult> res = it.check(node);
            if (res.getKey() > CheckResult.INFO) {
                checkResults.add(res.getRight());
            }
        });
        return super.visitClass(node, aVoid);
    }

    @Override
    public Void visitVariable(VariableTree node, Void aVoid) {
        variableChecks.stream().forEach(it -> {
            Pair<Integer, CheckResult> res = it.check(node);
            if (res.getKey() > CheckResult.INFO) {
                checkResults.add(res.getRight());
            }
        });
        return super.visitVariable(node, aVoid);
    }

    @Override
    public Void visitCompilationUnit(CompilationUnitTree node, Void aVoid) {
        compilationChecks.stream().forEach(it -> {
            Pair<Integer, CheckResult> res = it.check(node);
            if (res.getKey() > CheckResult.INFO) {
                checkResults.add(res.getRight());
            }
        });
        return super.visitCompilationUnit(node, aVoid);
    }

    public List<CheckResult> getCheckResults() {
        return checkResults;
    }
}
