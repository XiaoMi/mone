package com.xiaomi.youpin.docean.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * @author goodjava@qq.com
 * @date 2020/7/2
 */
public class ClassPathResource implements Resource {

    private String classPath;

    private InputStream inputStream;

    public ClassPathResource(String classPath) {
        this.classPath = classPath;
        inputStream = ClassPathResource.class.getClassLoader().getResourceAsStream(classPath);
    }

    public ClassPathResource(String classPath, ClassLoader cl) {
        if (null == cl) {
            cl = ClassPathResource.class.getClassLoader();
        }
        this.classPath = classPath;
        inputStream = cl.getResourceAsStream(classPath);
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public URL getURL() throws IOException {
        return null;
    }

    @Override
    public URI getURI() throws IOException {
        return null;
    }

    @Override
    public File getFile() throws IOException {
        return null;
    }

    @Override
    public long contentLength() throws IOException {
        return 0;
    }

    @Override
    public long lastModified() throws IOException {
        return 0;
    }

    @Override
    public String getFilename() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.inputStream;
    }

    @Override
    public String toString() {
        return this.classPath;
    }
}
