package com.xiaomi.youpin.docean.common;

import com.xiaomi.youpin.docean.exception.DoceanException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author goodjava@qq.com
 * @date 2020/6/22
 */
@Slf4j
public class ClassFinder {


    public Set<String> findClassSet(String packageName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return findClassSet(packageName, classLoader);
    }


    public Set<String> findClassSet(String packageName, ClassLoader classLoader) {
        if (null == classLoader) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        packageName = packageName.replace(".", "/");
        try {
            Enumeration resources = classLoader.getResources(packageName);
            Set<String> result = new HashSet<>();
            while (resources.hasMoreElements()) {
                URL resource = (URL) resources.nextElement();
                if (resource != null) {
                    String protocol = resource.getProtocol();
                    if ("file".equals(protocol)) {
                        findClassesByFile(packageName, resource.getPath(), result);
                    } else if ("jar".equals(protocol)) {
                        JarFile jar = ((JarURLConnection) resource.openConnection()).getJarFile();
                        log.info("findClassSet jar:{}", jar.getName());
                        findClassesByJar(packageName, jar, result);
                    }
                }
            }
            return result;
        } catch (Throwable ex) {
            throw new DoceanException(ex);
        }
    }

    private void findClassesByFile(String packageName, String resource, Set<String> result) {
        File directory = new File(resource);
        File[] listFiles = directory.listFiles();
        for (File file : listFiles) {
            if (file.isDirectory()) {
                findClassesByFile(packageName, file.getPath(), result);
            } else {
                String path = file.getPath();
                if (path.endsWith(".class")) {
                    int packageIndex = path.indexOf(packageName.replace("/", File.separator));
                    String classPath = path.substring(packageIndex, path.length() - 6);
                    result.add(classPath.replace(File.separator, "."));
                }
            }
        }
    }

    private static void findClassesByJar(String packageName, JarFile jar, Set<String> classes) {
        Enumeration<JarEntry> entry = jar.entries();
        JarEntry jarEntry;
        String name;
        while (entry.hasMoreElements()) {
            jarEntry = entry.nextElement();
            name = jarEntry.getName();
            if (name.charAt(0) == '/') {
                name = name.substring(1);
            }
            if (jarEntry.isDirectory() || !name.startsWith(packageName) || !name.endsWith(".class")) {
                continue;
            }
            String className = name.substring(0, name.length() - 6);
            classes.add(className.replace("/", "."));
        }
    }
}
