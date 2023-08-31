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

package com.xiaomi.youpin.codecheck.code.impl.youpin;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.tree.JCTree;
import com.xiaomi.youpin.codecheck.code.impl.CompilationCheck;
import com.xiaomi.youpin.codecheck.po.CheckResult;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class DubboMethodMustReturnResultRule extends CompilationCheck {
    private static final String DESC = "dubbo method should return class Result<>";
    private static final String CHINA_DESC = "dubbo接口返回参数类型必须是Result";

    @Override
    public Pair<Integer, CheckResult> _check(CompilationUnitTree compilationUnitTree) {
        List<? extends ImportTree> imports = compilationUnitTree.getImports();

        if (imports != null
                && imports.stream().anyMatch(it -> ((ImportTree) it).getQualifiedIdentifier().toString().equals("org.apache.dubbo.config.annotation.Service"))) {

            List<? extends Tree> typeDecls = compilationUnitTree.getTypeDecls();
            if (typeDecls == null || typeDecls.size() == 0) {
                return Pair.of(CheckResult.INFO, CheckResult.getInfoRes("DubboMethodMustReturnResultRule", "", ""));
            }
            for (Tree typeDecl : typeDecls) {
                if (typeDecl instanceof JCTree.JCClassDecl) {
                    JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) typeDecl;
                    com.sun.tools.javac.util.List<JCTree> trees = classDecl.getMembers();

                    if (trees == null || trees.size() == 0) {
                        return Pair.of(CheckResult.INFO, CheckResult.getInfoRes("DubboMethodMustReturnResultRule", "", ""));
                    }

                    for (JCTree tree : trees) {
                        if (tree instanceof JCTree.JCMethodDecl) {
                            String s = ((JCTree.JCMethodDecl) tree).getReturnType().toString();

                            if (!s.startsWith("Result<") || !s.endsWith(">")) {
                                return Pair.of(CheckResult.WARN, CheckResult.getWarnRes("method name: " + ((JCTree.JCMethodDecl) tree).getName().toString(), DESC, CHINA_DESC));
                            }
                        }
                    }
                }
            }
        }

        return Pair.of(CheckResult.INFO, CheckResult.getInfoRes("DubboMethodMustReturnResultRule", "", ""));
    }
}
