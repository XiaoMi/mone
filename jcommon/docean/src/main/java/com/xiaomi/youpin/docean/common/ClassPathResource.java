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
