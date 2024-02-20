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

package run.mone.m78.ip.common;

import com.intellij.openapi.diagnostic.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * @Author goodjava@qq.com
 */
public class Mp3PlayerV2 {


    private static final Logger log = Logger.getInstance(Mp3PlayerV2.class);

    private boolean begin;


    private Mp3PlayerV2() {

    }


    private static final class LazyHolder {
        private static Mp3PlayerV2 ins = new Mp3PlayerV2();
    }

    public static final Mp3PlayerV2 ins() {
        return LazyHolder.ins;
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        Mp3PlayerV2 player = new Mp3PlayerV2();
        player.play(new FileInputStream(new File("/tmp/a.mp3")));
        TimeUnit.SECONDS.sleep(5);
        player.close();
        System.out.println("close");
        TimeUnit.SECONDS.sleep(5);
    }


    public synchronized void close() {
        if (begin) {
            begin = false;
        }
    }

    public synchronized void play(InputStream input) {
    }

    public synchronized boolean isComplete() {
        return true;
    }


}
