///*
// *  Copyright 2020 Xiaomi
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//package com.xiaomi.youpin.gwdash.service;
//
//import com.alibaba.nacos.api.config.annotation.NacosValue;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.ks3.KsyunService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.util.DigestUtils;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.annotation.PostConstruct;
//import java.io.*;
//
//@Service
//@Slf4j
//public class UploadService {
//
//    private KsyunService ksyunService;
//
//    @NacosValue("${ks3.AccessKeyID}")
//    private String accesskey;
//
//    @NacosValue("${ks3.AccessKeySecret}")
//    private String accessSecret;
//
//    @PostConstruct
//    private void init() {
//        ksyunService = new KsyunService();
//        ksyunService.setAccessKeyID(accesskey);
//        ksyunService.setAccessKeySecret(accessSecret);
//        ksyunService.init();
//    }
//
//    public Result<String> uploadFile(MultipartFile file) {
//        String url = "";
//        try {
//            File tmpFile = File.createTempFile("upload-file-", file.getOriginalFilename());
//            OutputStream os = new FileOutputStream(tmpFile);
//            os.write(file.getBytes());
//            os.flush();
//            String fileMd5 = DigestUtils.md5DigestAsHex(new FileInputStream(tmpFile));
//            log.info("uploadImage md5: {}", fileMd5);
//            url = ksyunService.uploadFile("/gwdash-server/md/file/" + fileMd5, tmpFile, 60 * 60 * 12 * 100);
//            boolean success = tmpFile.delete();
//            if (!success) {
//                log.warn("UploadService delete temp file failed for file with original name: " + file.getOriginalFilename());
//            }
//        } catch (IOException e) {
//            log.info("UploadService#uploadFile: {}", e);
//        }
//        return Result.success(url);
//    }
//}
