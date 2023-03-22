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

import lombok.SneakyThrows;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * 判断依赖的rpc包是否加在了shade插件里面
 * @author zhangping17
 */
@Mojo( name = "jarCheck", defaultPhase = LifecyclePhase.VALIDATE, requiresDependencyResolution = ResolutionScope.COMPILE )
public class JarCheckMojo extends AbstractMojo {
    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${basedir}")
    private File outputDirectory;

    @Parameter(defaultValue = "${project}")
    public MavenProject project;

    @Parameter( defaultValue = "${project.compileClasspathElements}", readonly = true, required = true )
    private List<String> compilePath;

    @Parameter(defaultValue = "${project.build.directory}", readonly = true)
    private File target;

    private final String dubboReference = "run.mone.sautumnn.springboot.starter.anno.DubboReference";

    private final String shadePluginKey = "org.apache.maven.plugins:maven-shade-plugin";

    @SneakyThrows
    public void execute() throws MojoExecutionException {
        Log log = getLog();
        log.info("=============================begin jarcheck：" +project.getName()+ "=============================");
        ClassLoader classLoader = getClassLoader(this.project);
        if (classLoader == null) {
            log.info("=============================end jarcheck：" +project.getName()+ "=============================");
            return;
        }
        List<String> classNames = getClassNames();
        if (classNames == null || classNames.isEmpty()) {
            log.info("=============================end jarcheck：" +project.getName()+ "=============================");
            return;
        }
        Class dubboAnno = null;
        try {
            dubboAnno = classLoader.loadClass(dubboReference);
        } catch (Exception e) {
            log.info("=============================end jarcheck：" +project.getName()+ "=============================");
            return;
        }
        Set<Class<?>> classSet = new HashSet<>();
        for (String className : classNames) {
            Class cl = classLoader.loadClass(className);
            Field[] fields = cl.getDeclaredFields();
            if (fields == null || fields.length ==0) {
                continue;
            }
            for (Field field : fields) {
                Annotation annotation = field.getDeclaredAnnotation(dubboAnno);
                if (annotation == null) {
                    continue;
                }
                Class type = field.getType();
                String path = type.getProtectionDomain().getCodeSource().getLocation().getFile();
                String localRepository = project.getProjectBuildingRequest().getLocalRepository().getBasedir();
                path = path.split(localRepository)[1];
                String[] arr = path.split(File.separator);
                String artifactId = arr[arr.length-3];
                String groupId = String.join(".",path.split(artifactId)[0].split(File.separator)).substring(1);
                for (MavenProject mavenProject : project.getParent().getCollectedProjects()) {
                    if (!mavenProject.getName().endsWith("-server")) {
                        continue;
                    }
                    String mavenShadePlugin = mavenProject.getPlugin(shadePluginKey).getConfiguration().toString();
                    if (!mavenShadePlugin.contains("<include>" + groupId + ":" + artifactId + "</include>")) {
                        throw new MojoExecutionException("请把依赖:" + "<include>" + groupId + ":" + artifactId + "</include>" + "放到" + mavenProject.getName() + "的pom文件maven-shade-plugin里面");
                    }
                }

            }
        }
        log.info("=============================end jarcheck：" +project.getName()+ "=============================");
    }

    /**
     * 获取maven的classloader和依赖的包环境
     * @param project
     * @return
     */
    private ClassLoader getClassLoader(MavenProject project) {
        try {
            List<MavenProject> mavenProjects = project.getCollectedProjects();
            // 所有的类路径环境，也可以直接用 compilePath
            List classpathElements = project.getCompileClasspathElements();
            classpathElements.add(project.getBuild().getOutputDirectory());
            if (classpathElements.size() == 2 && classpathElements.get(0) == classpathElements.get(1)) {
                return null;
            }
            URL urls[] = new URL[classpathElements.size()+1];
            for (int i = 0; i < classpathElements.size(); ++i) {
                urls[i] = new File((String) classpathElements.get(i)).toURL();
            }
            // 自定义类加载器
            return new URLClassLoader(urls, this.getClass().getClassLoader());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getClassNames() {
        List<String> list = new ArrayList<>();
        if (target == null || target.listFiles() == null) {
            return list;
        }
        for (File file : target.listFiles()) {
            if (!file.getName().equals("classes")) {
                continue;
            }
            getClassName(file, list);
        }
        return list;
    }

    private List<String> getClassName(File file, List<String> classNames) {
        for (File sub : file.listFiles()) {
            if (sub.isDirectory()) {
                getClassName(sub, classNames);
            } else if (sub.getName().endsWith(".class")) {
                classNames.add(sub.getPath().split("classes/")[1].replace("/",".").split(".class")[0]);
            }
        }
        return classNames;
    }
}
