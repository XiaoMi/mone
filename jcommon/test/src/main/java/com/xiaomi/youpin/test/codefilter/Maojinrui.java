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

package com.xiaomi.youpin.test.codefilter;

/**
 * @author maojinrui
 */
public class Maojinrui {
    /**
     * {"code":600}
     */
    public static Integer code(byte[] data) {
        byte[] code = {0, 0, 0, 0, 0};
        code[0] = 'c';
        code[1] = 'o';
        code[2] = 'd';
        code[3] = 'e';
        code[4] = '"';

        StringBuilder sb = new StringBuilder();
        Integer nestedNumber = 0;

        for (int i = 0; i < data.length - code.length; i++) {
            if (data[i] == '{') {
                nestedNumber++;
            }
            if (data[i] == '}') {
                nestedNumber--;
            }
            if (nestedNumber > 1) {
                continue;
            }
            for (int j = 0; j < code.length; j++) {
                if (data[i + j] != code[j]) {
                    break;
                }
            }
            int k = i + code.length;
            while (k < data.length && data[k] == (byte) ' ') {
                k++;
            }
            if (k == data.length) {
                break;
            }
            if (data[k++] != ':') {
                continue;
            }
            while (k < data.length && data[k] == (byte) ' ') {
                k++;
            }
            if (data[k] >= '0' && data[k] <= '9') {
                while (data[k] >= '0' && data[k] <= '9') {
                    sb.append((char) data[k]);
                    k++;
                }
                if (data[k] != ' ' && data[k] != ',' && data[k] != '}') {
                    sb = null;
                }
                break;
            } else {
                continue;
            }
        }
        if (!"".equals(sb.toString())) {
            return Integer.valueOf(sb.toString());
        }
        return null;
    }
}
