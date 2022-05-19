package com.xiaomi.youpin.codecheck.code.impl.youpin;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.tree.JCTree;
import com.xiaomi.youpin.codecheck.code.impl.CompilationCheck;
import com.xiaomi.youpin.codecheck.po.CheckResult;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class DubboProNeedHealthMethod extends CompilationCheck {
    private static final String DESC = "dubbo project should better have health interface";
    private static final String CHINA_DESC = "dubbo项目需要加health健康监测接口";

    @Override
    public Pair<Integer, CheckResult> _check(CompilationUnitTree compilationUnitTree) {
        List<? extends ImportTree> imports = compilationUnitTree.getImports();

        if (imports != null
                && imports.stream().anyMatch(it -> ((ImportTree) it).getQualifiedIdentifier().toString().equals("org.apache.dubbo.config.annotation.Service"))) {

            List<? extends Tree> typeDecls = compilationUnitTree.getTypeDecls();
            if (typeDecls == null || typeDecls.size() == 0) {
                return Pair.of(CheckResult.WARN, CheckResult.getInfoRes("DubboProNeedHealthMethod", "", ""));
            }
            int count = 0;
            for (Tree typeDecl : typeDecls) {
                if (typeDecl instanceof JCTree.JCClassDecl) {
                    JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) typeDecl;
                    com.sun.tools.javac.util.List<JCTree> trees = classDecl.getMembers();

                    if (trees == null || trees.size() == 0) {
                        continue;
                    }

                    for (JCTree tree : trees) {
                        if (tree instanceof JCTree.JCMethodDecl) {
                            String s = ((JCTree.JCMethodDecl) tree).getName().toString();

                            if (s.equals("health")) {
                                count++;
                            }
                        }
                    }
                }
            }

            if (count == 0) {
                return Pair.of(CheckResult.WARN, CheckResult.getWarnRes("DubboProNeedHealthMethod", DESC, CHINA_DESC));
            }
        }

        return Pair.of(CheckResult.INFO, CheckResult.getInfoRes("DubboProNeedHealthMethod", "", ""));
    }
}
