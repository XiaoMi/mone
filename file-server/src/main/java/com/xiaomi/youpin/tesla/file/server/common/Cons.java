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

package com.xiaomi.youpin.tesla.file.server.common;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author goodjava@qq.com
 */
public abstract class Cons {

    public static final String TOKEN = Config.ins().get("token", "");

    public static final String DATAPATH = Config.ins().get("datapath", "/tmp");

    public static final String SERVER_PORT = Config.ins().get("server_port", "9999");

    public static final int CLEAN_NUM = Config.ins().getInt("cleanNum", 50);

    public static final String UPLOAD = "/upload";

    public static final String DOWNLOAD = "/download";

    public static final String GETTOKEN = "/token";

    public static final String HEALTH = "/health";
    
    public static final String LIST_FILES = "/list";
    
    public static final String DELETE_FILE = "/delete";

    public static final int WRITELIMIT = Config.ins().getInt("writeLimit", 10);

    public static final int READLIMIT = Config.ins().getInt("readLimit", 10);

    public static final int SO_SNDBUF = Config.ins().getInt("so_sndbuf", 65535);

    public static final int SO_RCVBUF = Config.ins().getInt("so_rcvbuf", 65535);

    /**
     * 是否开启限流
     */
    public static final boolean LIMIT = Config.ins().getBool("limit", false);

    public static final int MAXCONTENTLENGTH = Config.ins().getInt("maxcontentlength", 300);

    /**
     * 是否开启ssl (https)
     */
    public static final boolean SSL = Config.ins().getBool("ssl", false);


    static {
        createDataPath();
    }

    private static void createDataPath() {
        File file = new File(DATAPATH);
        if (!file.exists()) {
            try {
                FileUtils.forceMkdir(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
