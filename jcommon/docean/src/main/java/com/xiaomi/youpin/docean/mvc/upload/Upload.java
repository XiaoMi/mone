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

package com.xiaomi.youpin.docean.mvc.upload;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import lombok.SneakyThrows;

import java.io.File;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2022/5/27
 * 性能一般(但简单)
 */
public class Upload {

    public static boolean isUpload(String uri) {
        return uri.equals(UploadCons.UPLOAD);
    }

    @SneakyThrows
    public static String upload(String directory, HttpRequest request) {
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
        List<InterfaceHttpData> datas = decoder.getBodyHttpDatas();
        String fileName = "";
        for (InterfaceHttpData data : datas) {
            if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                FileUpload fileUpload = (FileUpload) data;
                fileName = fileUpload.getFilename();
                if (fileUpload.isCompleted()) {
                    if (!checkFileName(fileName)) {
                        throw new RuntimeException("fileName error:" + fileName);
                    }
                    fileUpload.renameTo(new File(directory + File.separator + fileName));
                }
            }
        }
        return fileName;
    }

    public static boolean checkFileName(String name) {
        String regex = "^[a-z0-9A-Z\\.]+$";
        return name.matches(regex);
    }
}
