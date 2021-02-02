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

import org.junit.Test;

import java.io.IOException;


/**
 * @author goodjava@qq.com
 */
public class FileUtilsTest {


    @Test
    public void testReadFileLines() throws IOException {
        long pointer = 1000;
        for (; ; ) {
            ReadResult res = FileUtils.readFile("/tmp/a", pointer, 1);
            res.getLines().forEach(System.out::println);
            pointer = res.getPointer();
            if (res.isOver()) {
                break;
            }
        }
    }


    @Test
    public void testList() throws IOException {
        FileUtils.list("/tmp").forEach(it -> {
            System.out.println(it.isFile() + "-->" + it.getName());
        });
    }
}
