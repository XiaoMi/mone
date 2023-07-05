/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.agent;

import com.xiaomi.mone.log.agent.channel.listener.DefaultFileMonitorListener;
import org.junit.Test;

import java.io.IOException;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/8/4 19:31
 */
public class DefaultFileMonitorListenerTest {

    @Test
    public void testFileChange() throws IOException {
        DefaultFileMonitorListener defaultFileMonitorListener = new DefaultFileMonitorListener();
        System.in.read();
    }
}
