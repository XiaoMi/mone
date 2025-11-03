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

package com.xiaomi.data.push.rpc.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 压缩工具类，使用 GZIP 压缩算法
 * 
 * @author goodjava@qq.com
 */
public class CompressionUtil {
    
    private static final Logger log = LoggerFactory.getLogger(CompressionUtil.class);
    
    /**
     * 压缩字节数组
     * 
     * @param data 原始数据
     * @return 压缩后的数据
     * @throws IOException 压缩异常
     */
    public static byte[] compress(byte[] data) throws IOException {
        if (data == null || data.length == 0) {
            return data;
        }
        
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
             GZIPOutputStream gzip = new GZIPOutputStream(bos)) {
            gzip.write(data);
            gzip.finish();
            byte[] compressed = bos.toByteArray();
            log.debug("Compressed data from {} bytes to {} bytes, compression ratio: {}%", 
                     data.length, compressed.length, 
                     String.format("%.2f", (1 - (double)compressed.length / data.length) * 100));
            return compressed;
        }
    }
    
    /**
     * 解压缩字节数组
     * 
     * @param compressedData 压缩的数据
     * @return 解压缩后的数据
     * @throws IOException 解压缩异常
     */
    public static byte[] decompress(byte[] compressedData) throws IOException {
        if (compressedData == null || compressedData.length == 0) {
            return compressedData;
        }
        
        try (ByteArrayInputStream bis = new ByteArrayInputStream(compressedData);
             GZIPInputStream gzip = new GZIPInputStream(bis);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzip.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            byte[] decompressed = bos.toByteArray();
            log.debug("Decompressed data from {} bytes to {} bytes", 
                     compressedData.length, decompressed.length);
            return decompressed;
        }
    }
}
