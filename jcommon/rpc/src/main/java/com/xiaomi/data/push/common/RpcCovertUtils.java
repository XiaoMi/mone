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

package com.xiaomi.data.push.common;

import com.xiaomi.data.push.uds.codes.CodeType;
import com.xiaomi.data.push.uds.codes.CodesFactory;
import com.xiaomi.data.push.uds.codes.ICodes;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 1/8/23
 */
@Slf4j
public abstract class RpcCovertUtils {

    public static Object[] convert(Class[] classes, byte[][] params) {
        ICodes codes = CodesFactory.getCodes(CodeType.PROTOSTUFF);
        return IntStream.range(0, classes.length).mapToObj(i -> codes.decode(params[i], classes[i])).toArray();
    }

}
