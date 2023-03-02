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

package com.xiaomi.youpin.codecheck;

import com.google.common.collect.Lists;
import com.xiaomi.youpin.codecheck.code.impl.*;
import com.xiaomi.youpin.codecheck.code.impl.classcheck.ClassPasswordCheck;
import com.xiaomi.youpin.codecheck.code.impl.flowcontrol.SwitchCheck;
import com.xiaomi.youpin.codecheck.code.impl.flowcontrol.SwitchStatementRule;
import com.xiaomi.youpin.codecheck.code.impl.naming.*;
import com.xiaomi.youpin.codecheck.code.impl.set.ContainerSizeCheck;
import com.xiaomi.youpin.codecheck.code.impl.set.LongVariableAvoidNoneL;
import com.xiaomi.youpin.codecheck.code.impl.youpin.DubboMethodMustReturnResultRule;
import com.xiaomi.youpin.codecheck.code.impl.youpin.DubboProNeedHealthMethod;
import com.xiaomi.youpin.codecheck.code.impl.youpin.IPRule;
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
            new AbstractClassNamingRule(),
            new ClassPasswordCheck()
    );

    public static List<CompilationCheck> compilationCheckList = Lists.newArrayList(
            new DubboMethodMustReturnResultRule(),
            new DubboProNeedHealthMethod(),
            new IPRule()
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

        //yml等配置文件校验
        ConfigCheck ipCheck = new ConfigCheck();
        Map<String, List<CheckResult>> configCheckMap = ipCheck.configCheck(path);
        if (!configCheckMap.isEmpty() && configCheckMap.size() > 0) {
            res.putAll(configCheckMap);
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
