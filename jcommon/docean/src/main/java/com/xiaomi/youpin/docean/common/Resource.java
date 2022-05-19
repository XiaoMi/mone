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
public interface Resource {

    boolean exists();

    default boolean isReadable() {
        return true;
    }

    default boolean isOpen() {
        return false;
    }

    default boolean isFile() {
        return false;
    }

    URL getURL() throws IOException;

    URI getURI() throws IOException;

    File getFile() throws IOException;

    long contentLength() throws IOException;

    long lastModified() throws IOException;

    String getFilename();

    String getDescription();

    InputStream getInputStream() throws IOException;

}
