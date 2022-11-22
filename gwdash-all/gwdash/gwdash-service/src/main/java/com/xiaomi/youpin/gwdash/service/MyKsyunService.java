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

package com.xiaomi.youpin.gwdash.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.xiaomi.youpin.ks3.KsyunService;

import javax.annotation.PostConstruct;

/**
 * @author tsingfu
 */
@Service
@Slf4j
public class MyKsyunService {

    private KsyunService ksyunService;

    @Value("${use.file.server}")
    private boolean useFileServer;

    @Value("${file.server.url}")
    private String fileServerUrl;


    @PostConstruct
    public void init() {
        log.info("useFileServer");
        ksyunService = new KsyunService(fileServerUrl);
        ksyunService.setToken("dprqfzzy123!");
    }

    public byte[] getFileByDownloadKey(String downloadKey) {
        return  ksyunService.getFileByKey(downloadKey);
    }
}
