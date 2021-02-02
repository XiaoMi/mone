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
import java.util.*;

/**
 * @author tsingfu
 */
public class RenQingFu {

    private Stack<Byte> stack = new Stack<Byte>();
    private Map<Byte, Byte> map = new HashMap<Byte, Byte>();
    private Set<Byte> spaces = new HashSet<Byte>();

    // {
    private final byte openBrace = "{".getBytes()[0];
    // }
    private final byte closeBrace = "}".getBytes()[0];
    // [
    private final byte leftSquareBracket = "[".getBytes()[0];
    // ]
    private final byte rightSquareBracket ="]".getBytes()[0];
    // "
    private final byte quotation = "\"".getBytes()[0];
    // \
    private final byte escape = "\\".getBytes()[0];
    // :
    private final byte colon = ":".getBytes()[0];
    // ,
    private final byte comma = ",".getBytes()[0];

    public RenQingFu() {
        map.put(openBrace, closeBrace);
        map.put(leftSquareBracket, rightSquareBracket);
        spaces.add((" ".getBytes()[0]));
        spaces.add(("\n".getBytes()[0]));
        spaces.add(("\t".getBytes()[0]));
    }

    public int[] getPositionOfCode(byte[] bytes) {
        int i = 0;
        // 删除空格
        for (; i < bytes.length; i++) {
            if (!spaces.contains(bytes[i])) {
                break;
            }
        }
        // {
        if (bytes[i] == openBrace) {
            stack.push((bytes[i]));
            return parserCode(bytes, i);
        }
        return null;
    }

    public int covertInt(byte[] bytes, int[] pos) {
        if (null == pos || pos.length <= 1) {
            throw new NumberFormatException("pos不合法");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = pos[0]; i < pos[1] && i < bytes.length; i++) {
            sb.append((char) bytes[i]);
        }
        return Integer.valueOf(sb.toString().trim());
    }

    private int[] parserCode (byte[] bytes, int start) {
        int i = start, len = bytes.length - 1;
        boolean isKey = false;
        while (i < len) {
            int[] pos = parserKey(bytes, i + 1);
            if (-1 == pos[0]) {
                return null;
            }
            i = positionOfColon(bytes, pos[1] + 1);
            if (i == -1) {
                return null;
            }
            if (4 == pos[1] - pos[0] && isKey(bytes, pos[0], pos[1])) {
                isKey = true;
            }
            pos = parserValue(bytes, i + 1);
            if (-1 == pos[0]) {
                return null;
            }
            // System.out.println(pos[0] + " " + pos[1]);
            if (isKey) {
                return pos;
            }
            i = pos[1];
        }
        return null;
    }

    private int[] parserKey(byte[] bytes, int start) {
        int i = start;
        int[] pos = new int[2];
        // 删除空格
        for (; i < bytes.length; i++) {
            if (!spaces.contains(bytes[i])) {
                break;
            }
        }
        // "
        if (bytes[i++] == quotation) {
            pos[0] = i;
            for (; i < bytes.length; i++) {
                // \" \\
                if (escape == bytes[i]) {
                    if (i + 1 < bytes.length && (
                            quotation == bytes[i + 1] || escape == bytes[i + 1])) {
                        i++;
                    } else {
                        pos[0] = -1;
                        return pos;
                    }
                } else if (quotation == bytes[i]) {
                    pos[1] = i;
                    break;
                }
            }
        }
        return pos;
    }

    private int positionOfColon(byte[] bytes, int start) {
        int i = start;
        // 删除空格
        for (; i < bytes.length; i++) {
            if (!spaces.contains(bytes[i])) {
                break;
            }
        }
        // :
        if (bytes[i] == colon) {
            return i;
        }
        return -1;
    }

    private int[] parserValue(byte[] bytes, int start) {
        int i = start;
        int[] pos = new int[2];
        // 删除空格
        for (; i < bytes.length; i++) {
            if (!spaces.contains(bytes[i])) {
                break;
            }
        }
        pos[0] = i;
        boolean isIn = false;
        // 寻找 ,
        for (i = i; i < bytes.length; i++) {
            if (isIn == false
                    && (bytes[i] == openBrace || bytes[i] == leftSquareBracket)) {
                stack.push(bytes[i]);
            }
            else if (quotation == bytes[i]) {
                isIn = !isIn;
            }
            // \" \\
            else if (escape == bytes[i]) {
                if (i + 1 < bytes.length
                        && (quotation == bytes[i + 1] || escape == bytes[i + 1])) {
                    i++;
                } else {
                    pos[0] = -1;
                    return pos;
                }
            }
            else if (isIn == false
                    && (bytes[i] == closeBrace || bytes[i] == rightSquareBracket)) {
                if (i >= bytes.length - 1 || stack.size() <= 1) {
                    pos[1] = i;
                    return pos;
                } else {
                    byte eByte = stack.pop();
                    if (map.get(eByte) != bytes[i]) {
                        pos[0] = -1;
                        return pos;
                    }
                }
            }
            else if (isIn == false && bytes[i] == comma) {
                if (stack.size() <= 1) {
                    pos[1] = i;
                    return pos;
                }
            }
        }
        pos[0] = -1;
        return pos;
    }

    private boolean isKey (byte[] bytes, int start, int end) {
        byte[] code = "code".getBytes();
        for (int i = start; i < end; i++) {
            if (bytes[i] != code[i-start]) {
                return false;
            }
        }
        return true;
    }

    public static int code (byte[] bytes) {
        RenQingFu findCode = new RenQingFu();
        try {
            int[] pos = findCode.getPositionOfCode(bytes);
            if (null == pos) {
                throw new NumberFormatException("not found code");
            }
            int c = findCode.covertInt(bytes, pos);
            return c;
        } catch (NumberFormatException e) {
            return 200;
        }
    }

    public static void main (String[] args) {
        String testStr = "{ \"code\":\"2a\"}";
        byte[] testCode = testStr.getBytes();
        System.out.println(code(testCode));
    }
}
