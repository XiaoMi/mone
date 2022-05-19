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
