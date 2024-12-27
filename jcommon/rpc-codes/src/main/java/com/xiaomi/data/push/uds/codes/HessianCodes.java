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

package com.xiaomi.data.push.uds.codes;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author goodjava@qq.com
 * @date 1/23/21
 */
@Slf4j
public class HessianCodes implements ICodes {
    @Override
    public <T> T decode(byte[] data, Type type) {
        ByteArrayInputStream is = new ByteArrayInputStream(data);
        Hessian2Input hi = new Hessian2Input(is);
        hi.setSerializerFactory(DefaultHessian2FactoryInitializer.getSerializerFactory());
        try {
            return (T) hi.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                hi.close();
                is.close();
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }
    }

    @Override
    public <T> byte[] encode(T t) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Hessian2Output ho = new Hessian2Output(os);
        ho.setSerializerFactory(DefaultHessian2FactoryInitializer.getSerializerFactory());
        try {
            ho.writeObject(t);
            ho.flush();
            return os.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                ho.close();
                os.close();
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }
    }

    @Override
    public byte type() {
        return CodeType.HESSIAN;
    }
}
