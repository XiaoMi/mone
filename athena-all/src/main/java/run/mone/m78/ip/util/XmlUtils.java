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

package run.mone.m78.ip.util;

import com.google.common.collect.Maps;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import run.mone.ultraman.AthenaContext;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
public class XmlUtils {



    public static Map<String, String> getKV(String filePath, String tagName) {
        Map<String, String> res = new HashMap<>(5);
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(filePath);

            NodeList l = document.getElementsByTagName(tagName);

            for (int i = 0; i < l.getLength(); i++) {
                Node node = l.item(i);
                NodeList c = node.getChildNodes();
                for (i = 0; i < c.getLength(); i++) {
                    Node n = c.item(i);
                    res.put(n.getNodeName(), n.getTextContent());
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return res;
    }


    /**
     * 获取mybatis 配置文件的位置
     *
     * @param projectName
     * @param moduleName
     * @return
     */
    public static String getGeneratorConfigPath(String projectName, String moduleName) {
        return ApplicationManager.getApplication().runReadAction((Computable<String>) () -> {
            Project project = AthenaContext.ins().getProjectMap().get(projectName);
            PsiDirectory directory = PsiClassUtils.getDirectory(project, moduleName, "/src/main/resources");
            @Nullable PsiFile file = directory.findFile("generatorConfig.xml");
            System.out.println(directory.getName() + file);
            return file.getVirtualFile().getPath();
        });
    }


    @SneakyThrows
    public static Map<String, String> getMysqlConfigFromMybatisConfig(String path) {
        return Maps.newHashMap();

    }


    public static void updateGeneratorConfig(String path, String newName) {

    }


}
