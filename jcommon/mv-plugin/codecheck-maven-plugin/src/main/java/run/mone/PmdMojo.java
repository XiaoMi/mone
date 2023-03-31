package run.mone;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.xiaomi.youpin.codecheck.CodeCheck;
import com.xiaomi.youpin.codecheck.CommonUtils;
import com.xiaomi.youpin.codecheck.po.CheckResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.*;

/**
 * @author zhangping17
 */
@Mojo( name = "pmd", defaultPhase = LifecyclePhase.VALIDATE )
public class PmdMojo extends AbstractMojo {
    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${basedir}")
    private File outputDirectory;

    /**
     * 是否检测配置文件，默认为true
     */
    @Parameter(property = "check.config" ,defaultValue = "true")
    private Boolean checkConfigurationFile;

    @Parameter
    private String[] ipWhite;

    private final static String level = "[ERROR]";

    public void execute() throws MojoExecutionException {
        getLog().info("=============================begin codecheck=============================");
        CodeCheck codeCheck = new CodeCheck();
        Map<String, List<CheckResult>> map = null;
        try {
            if (ipWhite != null) {
                CommonUtils.addIpWhite(Arrays.asList(ipWhite));
            }
            if (checkConfigurationFile != null && !checkConfigurationFile) {
                map = codeCheck.check(outputDirectory.getPath(), false);
            } else {
                map = codeCheck.check(outputDirectory.getPath(), true);
            }

        } catch (Exception e) {
            getLog().error(e);
        }

        if (map == null || map.isEmpty()) {
            getLog().info("=============================end codecheck=============================");
            return;
        }
        List<String> results = new ArrayList<>();
        String tmpResult = null;
        for (Map.Entry entry : map.entrySet()) {
            List<CheckResult> checkResultList = (List<CheckResult>) entry.getValue();
            if (checkResultList == null || checkResultList.isEmpty()) {
                continue;
            }
            for (CheckResult checkResult : checkResultList) {
                if (StringUtils.isNotEmpty(checkResult.getLevel()) && level.equals(checkResult.getLevel().toUpperCase())) {
                    tmpResult = entry.getKey() + ":" + checkResult;
                    results.add(tmpResult);
                }
            }
        }
        if (results.isEmpty()) {
            getLog().info("=============================end codecheck=============================");
            return;
        }
        String result = String.join("\n", results);
        getLog().info("=============================end codecheck=============================");
        throw new RuntimeException(result);
    }
}
