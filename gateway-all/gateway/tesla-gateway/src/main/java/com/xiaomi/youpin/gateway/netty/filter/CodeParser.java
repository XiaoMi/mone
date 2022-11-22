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

package com.xiaomi.youpin.gateway.netty.filter;

//import com.dianping.cat.Cat;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Zheng Xu zheng.xucn@outlook.com
 * @modify goodjava@qq.com
 * 为了追求性能,不解出来结果,而是一个字符一个字符的分析
 * 有可能不是标准的json,而判定正确
 */
@Slf4j
public class CodeParser {

    private static final byte[] specials = {(byte) '{', (byte) '}', (byte) '[', (byte) ']', (byte) '\'', (byte) '\"'};
    private static final byte[] acceptableBytes = {(byte) ',', (byte) ' ', (byte) '\'', (byte) '\"', (byte) '}', (byte) '\n', (byte) '\t'};
    private static final byte[] pattern = {99, 111, 100, 101};
    private static final int DEFAULT_CODE = 0;

    public static int parseCode(ByteBuf buf) {
        try {
            return code(buf);
        } catch (Exception e) {
            log.error(e.getMessage());
//            Cat.logError(e);
            return -1;
        }
    }

    private static int code(ByteBuf A) {
        if (A == null || A.readableBytes() == 0) {
            return DEFAULT_CODE;
        }
        // code =99 111 100 101

        int startIndex = startIndex(A);
        int endIndex = endIndex(A);
        if (!isJson(A, startIndex, endIndex)) {
            return DEFAULT_CODE;
        }
        startIndex++;
        endIndex--;

        boolean colon = false;
        int levels = 0;
        for (int i = startIndex; i <= (endIndex + 1) - pattern.length; i++) {

            byte b = A.getByte(i);

            //used for debugging
            char c = (char) b;

            if (levels == 0 && b == (byte) ':') {
                colon = true;
                continue;
            }
            if (colon && isSpecial(b)) {
                if (isQuotation(b)) {
                    i++;
                    //找出匹配的引号
                    while (!(A.getByte(i) == b && (i - 1 < 0 || A.getByte(i - 1) != (byte) '\\'))) {
                        i++;
                    }
                } else {
                    if (isLeft(b)) {
                        // { [
                        levels++;
                    } else {
                        // } ]
                        levels--;
                    }
                }
                if (levels == 0) {
                    colon = false;
                    continue;
                }
            }

            if (levels > 0) {
                continue;
            }

            if (colon && A.getByte(i) == (byte) ',') {
                colon = false;
                continue;
            }

            int nextI = i;
            if (!colon && isQuotation(b)) {
                //处理有引号的key
                int j = i + 1;
                //找出匹配的引号
                while (!(A.getByte(j) == b && (j - 1 < 0 || A.getByte(j - 1) != (byte) '\\'))) {
                    j++;
                }

                int keyLength = j - i + 1;
                //检测key的长度是否等于４
                if (keyLength != pattern.length + 2) {
                    i = j;
                    continue;
                }
                i++;
                nextI = j;
            }

            //处理没有引号的key
            int code = processKey(i, A);
            if (code != -1) {
                return code;
            }
            i = nextI;
        }

        return DEFAULT_CODE;
    }

    /**
     * 处理key
     *
     * @param index
     * @param A
     * @return
     */
    private static int processKey(int index, ByteBuf A) {
        boolean match = patternSearch(index, A);

        if (match && isValidKey(index, A)) {
            int code = parseCode(A, index + pattern.length);
            if (code != -1) {
                return code;
            }
            return DEFAULT_CODE;
        }
        return -1;
    }

    /**
     * 检测key是否是'code'
     *
     * @param i 初始index
     * @param A 　byte array
     * @return 返回true如果key等于code
     */
    private static boolean patternSearch(int i, ByteBuf A) {
        for (int j = 0; j < pattern.length; j++) {
            if (A.getByte(i + j) != pattern[j]) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidKey(int i, ByteBuf A) {
        int index = i - 1;
        return index >= 0 && (A.getByte(index) == (byte) ',' || A.getByte(index) == (byte) '{' || A.getByte(index) == (byte) ' ' || A.getByte(index) == (byte) '\'' || A.getByte(index) == (byte) '\"');
    }

    private static int parseCode(ByteBuf A, int start) {
        final int DEFAULT_CODE = -1;
        int i = start;

        //找冒号
        while (i < A.readableBytes() && A.getByte(i) != (byte) ':') {
            if (!(A.getByte(i) == (byte) ' ' || A.getByte(i) == (byte) '\'' || A.getByte(i) == (byte) '\"' || A.getByte(i) == (byte) '\t' || A.getByte(i) == (byte) '\n')) {
                return DEFAULT_CODE;
            }
            i++;
        }
        i++;

        //找数字的开始
        while (i < A.readableBytes() && !isNumber(A.getByte(i))) {
            if (!(A.getByte(i) == (byte) ' ' || A.getByte(i) == (byte) '\'' || A.getByte(i) == (byte) '\"' || A.getByte(i) == (byte) '\n' || A.getByte(i) == (byte) '\t')) {
                return DEFAULT_CODE;
            }
            i++;
        }

        //Integer.parseInt
        int code = 0;
        boolean hasNumber = false;
        while (i < A.readableBytes() && isNumber(A.getByte(i))) {
            code = code * 10 + toDigit(A.getByte(i));
            i++;
            hasNumber = true;
        }

        return validateCode(hasNumber, i, A, code);
    }

    private static int validateCode(boolean hasNumber, int i, ByteBuf A, int code) {
        if (!hasNumber) {
            return -1;
        }

        if (i < A.readableBytes()) {
            for (byte b : acceptableBytes) {
                if (A.getByte(i) == b) {
                    if (code < 0) {
                        //检测 integer overflow
                        code = DEFAULT_CODE;
                    }
                    return code;
                }
            }
        }
        return -1;
    }

    private static int toDigit(byte a) {
        return a - (byte) '0';
    }

    private static boolean isNumber(byte a) {
        return a >= (byte) '0' && a <= (byte) '9';
    }

    private static boolean isJson(ByteBuf A, int startIndex, int endIndex) {
        if (startIndex == A.readableBytes() || endIndex < 0) {
            return false;
        }

        byte left = (byte) '{';
        byte right = (byte) '}';
        return A.getByte(startIndex) == left && A.getByte(endIndex) == right;
    }

    private static int startIndex(ByteBuf A) {
        int i = 0;
        while (i < A.readableBytes() && A.getByte(i) != (byte) '{' && A.getByte(i) != (byte) '[') {
            i++;
        }
        return i;
    }

    private static int endIndex(ByteBuf A) {
        int i = A.readableBytes() - 1;
        while (i >= 0 && A.getByte(i) != (byte) '}' && A.getByte(i) != (byte) ']') {
            i--;
        }
        return i;
    }

    private static boolean isSpecial(byte b) {
        for (byte a : specials) {
            if (a == b) {
                return true;
            }
        }
        return false;
    }

    private static boolean isQuotation(byte b) {
        return b == (byte) '\"' || b == (byte) '\'';
    }

    private static boolean isLeft(byte b) {
        return b == (byte) '{' || b == (byte) '[';
    }
}
