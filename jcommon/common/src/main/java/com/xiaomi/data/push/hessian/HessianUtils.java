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

package com.xiaomi.data.push.hessian;


import com.alibaba.com.caucho.hessian.io.HessianInput;
import com.alibaba.com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author goodjava@qq.com
 */
public class HessianUtils {


    private HessianUtils() {

    }

    public static byte[] write(Object obj) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HessianOutput ho = new HessianOutput(os);
        try {
            ho.writeObject(obj);
            return os.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                ho.close();
                os.close();
            } catch (Exception ex) {
                //ignore
            }
        }
    }


    public static Object read(byte[] data) {
        ByteArrayInputStream is = new ByteArrayInputStream(data);
        HessianInput hi = new HessianInput(is);
        try {
            return hi.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                hi.close();
                is.close();
            } catch (Exception ex) {
                //ignore
            }
        }
    }
}
