package com.xiaomi.youpin.test.codefilter;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Stack;

public class Dingpei {


    public static int code(byte[] bytes) throws UnsupportedEncodingException {
        byte space = getByteArr(" ")[0];
        byte left1 = getByteArr("{")[0];
        byte left2 = getByteArr("[")[0];
        byte right1 = getByteArr("}")[0];
        byte right2 = getByteArr("]")[0];
        byte c = getByteArr("c")[0];
        byte quotation = getByteArr("\"")[0];
        byte slash = getByteArr("\\")[0];
        byte[] code = getByteArr("code\"");
        byte zero = getByteArr("0")[0];
        byte nine = getByteArr("9")[0];

        if (bytes == null
                || bytes.length == 0) {
            return 200;
        }

        try {
            Stack<Character> stack = new Stack();
            if (bytes[0] == left1) {
                stack.push('{');
            }
            for (int i = 1; i < bytes.length; i++) {

                if (bytes[i] == left1 && stack.peek() != '"') {
                    stack.push('{');
                }
                if (bytes[i] == right1) {
                    if (stack.peek() != '{') {
                        continue;
                    } else {
                        stack.pop();
                    }
                }

                if (bytes[i] == left2 && stack.peek() != '"') {
                    stack.push('[');
                }
                if (bytes[i] == right2) {
                    if (stack.peek() != '[') {
                        continue;
                    } else {
                        stack.pop();
                    }
                }

                if (bytes[i] == quotation && bytes[i - 1] != slash) {
                    if (stack.peek() != '"') {
                        stack.push('"');
                    } else {
                        stack.pop();
                    }
                }


                if (bytes[i] == c && stack.size() == 2 && stack.peek() == '"') {
                    if (i + 4 < bytes.length) {
                        byte[] tmp = {bytes[i], bytes[i + 1], bytes[i + 2], bytes[i + 3], bytes[i + 4]};
                        if (Arrays.equals(code, tmp)) {
                            int begin = i;
                            for (int j = i + 5; j < bytes.length; j++) {
                                if (bytes[j] >= zero && bytes[j] <= nine) {
                                    begin = j;
                                    break;
                                }
                            }
                            int end = begin;
                            for (int j = begin; j < bytes.length; j++) {
                                if (bytes[j] < zero || bytes[j] > nine) {
                                    end = j - 1;
                                    break;
                                }
                            }
                            byte[] codebytes = new byte[end - begin + 1];
                            System.arraycopy(bytes, begin, codebytes, 0, end - begin + 1);
                            return Integer.valueOf(new String(codebytes));
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            return 200;
        }

        return 200;
    }


    private static byte[] getByteArr(String s) throws UnsupportedEncodingException {
        return s.getBytes("UTF-8");
    }
}
