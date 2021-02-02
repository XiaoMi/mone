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

package com.xiaomi.youpin.tesla.bug.common;

import com.xiaomi.youpin.tesla.bug.exception.BugException;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.beans.BeanCopier;

/**
 * @author goodjava@qq.com
 * @date 2020/9/5
 */
@Slf4j
public abstract class CopyUtil {

    public static <T> T copy(Class source, Class<T> target, Object osource, Object otarget) {
        try {
            BeanCopier copier = BeanCopier.create(source, target, false);
            copier.copy(osource, otarget, null);
            return (T) otarget;
        } catch (Throwable ex) {
            log.error("copy error:{}", ex.getMessage());
            throw new BugException(ex);
        }
    }


}
