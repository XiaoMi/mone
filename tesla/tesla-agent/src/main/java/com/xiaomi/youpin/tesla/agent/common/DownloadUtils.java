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

package com.xiaomi.youpin.tesla.agent.common;

import com.xiaomi.data.push.client.HttpClientV2;
import com.xiaomi.youpin.tesla.agent.exception.AgentException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public abstract class DownloadUtils {

    private static ExecutorService downloadPool = Executors.newFixedThreadPool(8);

    private static final boolean useSdk = Boolean.parseBoolean(Config.ins().get("useSdk", "false"));

    /**
     * 下载文件会重试3次
     * <p>
     * 超时时间单位是秒
     *
     * @param downloadKey
     * @return
     */
    public static byte[] download(final String downloadKey, final String url, final long timeout, File file) {
        for (int i = 0; i < 3; i++) {
            Future<byte[]> future = null;
            try {
                future = downloadPool.submit(() -> {
                    if (useSdk) {
                        YoupinKsYunService.ins().ksyunService.getFileByKey(downloadKey, file);
                        return new byte[]{};
                    }
                    return HttpClientV2.download(url, 2000, file);
                });
                byte[] data = future.get(timeout, TimeUnit.SECONDS);
                return data;
            } catch (Throwable ex) {
                log.warn("downlaod file:{} error:{} {}", downloadKey, ex.getMessage(), i);
                future.cancel(true);
            }
            CommonUtils.sleep(2);
        }
        throw new AgentException("download file error:" + downloadKey + ":" + url);
    }


}
