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
package com.xiaomi.mone.log.common;

import com.xiaomi.mone.log.utils.IndexUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/12/29 12:42
 */
@Slf4j
public class IndexUtilsTest {

    @Test
    public void testGetKeyValueList(){
        String keyList = "timestamp:1,level:1,traceId:1,threadName:1,className:1,message:1,line:1,methodName:1,logstore:3,logsource:3,mqtopic:3,mqtag:3,logip:3,tail:3";
        String valueList = "0,2,1,6,3,4,5,-1";
        String keyValueList = IndexUtils.getKeyValueList(keyList, valueList);
        log.info(keyValueList);
    }
}
