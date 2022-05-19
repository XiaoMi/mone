package com.xiaomi.youpin.codecheck;

import com.google.common.collect.Lists;
import com.xiaomi.youpin.codecheck.code.impl.*;
import com.xiaomi.youpin.codecheck.code.impl.naming.*;
import com.xiaomi.youpin.codecheck.code.impl.set.ContainerSizeCheck;
import com.xiaomi.youpin.codecheck.code.impl.flowcontrol.SwitchCheck;
import com.xiaomi.youpin.codecheck.code.impl.flowcontrol.SwitchStatementRule;
import com.xiaomi.youpin.codecheck.code.impl.set.LongVariableAvoidNoneL;
import com.xiaomi.youpin.codecheck.code.impl.youpin.DubboMethodMustReturnResultRule;
import com.xiaomi.youpin.codecheck.code.impl.youpin.DubboProNeedHealthMethod;
import com.xiaomi.youpin.codecheck.docCheck.JavaDocReader;
import com.xiaomi.youpin.codecheck.po.CheckResult;
import com.xiaomi.youpin.codecheck.pomCheck.PomCheck;
import com.xiaomi.youpin.codecheck.visitor.SourceVisitor;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeCheck implements Serializable {

    public static List<MethodCheck> methodCheckList = Lists.newArrayList(
            new SwitchStatementRule(),
            new SwitchCheck()
    );

    public static List<ClassCheck> classCheckList = Lists.newArrayList(
            new ClassNamingShouldBeCamelRule(),
            new AbstractClassNamingRule()
    );

    public static List<CompilationCheck> compilationCheckList = Lists.newArrayList(
            new DubboMethodMustReturnResultRule(),
            new DubboProNeedHealthMethod()
    );

    public static List<VariableCheck> variableCheckList = Lists.newArrayList(
            new AvoidStartWithDollarAndUnderLineNamingRule(),
            new ContainerSizeCheck(),
            new VariableNamingShouldBeLowerCamelRule(),
            new LongVariableAvoidNoneL(),
            new ConstantVariableNamingRule()
    );



    public Map<String, List<CheckResult>> check(String path) {
        Map<String, List<CheckResult>> res = new HashMap<>();

        if (path == null || path.equals("")) {
            return res;
        }

        //xxx.java校验
        List<File> files = CommonUtils.searchFiles(new File(path), ".java");
        files.stream().forEach(it -> {
            SourceVisitor sourceVisitor = new SourceVisitor(methodCheckList, classCheckList, variableCheckList, compilationCheckList);
            JavaSourceCheck javaSourceCheck = new JavaSourceCheck(it.getPath(), sourceVisitor);
            javaSourceCheck.check();
            List<CheckResult> checkResultList = sourceVisitor.getCheckResults();
            if (checkResultList.size() != 0) {
                res.put(it.getPath(), checkResultList);
            }

            Pair<Integer, CheckResult> javaDocRes = JavaDocReader.check(it.getPath());
            if (javaDocRes.getKey() > CheckResult.INFO) {
                res.put(it.getPath(),  Lists.newArrayList(javaDocRes.getRight()));
            }
        });

        //pom.xml校验
        PomCheck pomCheck = new PomCheck();
        Map<String, List<CheckResult>> pomCheckMap = pomCheck.pomCheck(path);
        if (pomCheckMap.size() > 0) {
            res.putAll(pomCheckMap);
        }

        return res;
    }
}
