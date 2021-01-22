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

package com.xiaomi.mone.file;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class FileUtils {


    public static ReadResult readFile(String file, long filePointer, int lineNum) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long length = raf.length();
            if (filePointer > length) {
                log.warn("filePointer > length");
                filePointer = 0;
            }
            //从文件未读取
            if (filePointer == -1) {
                filePointer = raf.length();
            }

            raf.seek(filePointer);
            String line = "";
            int n = 1;
            List<String> lines = new ArrayList<>();
            boolean over = true;
            while ((line = raf.readLine()) != null) {
                lines.add(line);
                if (++n > lineNum) {
                    over = false;
                    break;
                }
            }
            long pointer = raf.getFilePointer();
            ReadResult readResult = new ReadResult();
            readResult.setPointer(pointer);
            readResult.setLines(lines);
            readResult.setOver(over);
            return readResult;
        }

    }


    public static List<MoneFile> list(String path) throws IOException {
        return Files.list(Paths.get(path)).map(it -> {
            File file = it.toFile();
            MoneFile mf = new MoneFile();
            mf.setFile(file.isFile());
            mf.setName(file.getName());
            return mf;
        }).collect(Collectors.toList());
    }

}
