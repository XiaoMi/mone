package run.mone.m78.ip.service;

import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/5/21 22:07
 */
public class XmlService {

    /**
     * 方便操纵xml,这里的作用是修改maven 中的 pom 加入依赖
     *
     * @param project
     * @param moduleName
     * @param dependency
     */
    public static void openMavenPomAndModify(Project project, String moduleName, String dependency) {

    }

    public static Pair<Integer, String> checkPomVersion(Project project, String groupId, String artifactId, String version) {
        return null;
    }

    @NotNull
    private static Pair<Integer, String> insertDependency(Project project, String groupId, String artifactId, String version, Map<String, String> modulePathMap) throws IOException, XmlPullParserException {
        return null;
    }

    private static Pair<Integer, String> writeDependency(Model model, File pomFile) {
        return null;
    }

    private static String buildDependencyString(String groupId, String artifactId, String version) {
        return String.format("```\n" +
                "<dependency>\n" +
                "    \t<groupId>%s</groupId>\n" +
                "    \t<artifactId>%s</artifactId>\n" +
                "    \t<version>%s</version>\n" +
                "</dependency>", groupId, artifactId, version);

    }


}
