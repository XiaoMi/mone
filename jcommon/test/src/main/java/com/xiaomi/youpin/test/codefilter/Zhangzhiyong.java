package com.xiaomi.youpin.test.codefilter;

import lombok.extern.slf4j.Slf4j;

import java.util.Stack;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class Zhangzhiyong {


    private static int findBegin(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] == '{') {
                return i;
            }
            if (data[i] == '[') {
                return -1;
            }
        }
        return -1;
    }


    private static int search(byte[] data, int b, int e) {

//        System.out.println(new String(data));
        //符号
        Stack<Byte> s = new Stack<>();
        Stack<Byte> s2 = new Stack<>();

        boolean k = true;

        int level = 0;

        for (int i = b; i < e; i++) {
            Byte d = data[i];

            char ccc = (char) (byte) d;

            if (d == '\\') {
                i++;
                s2.push((byte) '*');
                continue;
            }

            //解决value="code" 的问题
            if (d == ':' && s.size() == 0) {
                k = false;
            }

            if (d == ',' && s.size() == 0) {
                k = true;
            }


            if (d == '\"') {
                //找到成对的
                if (s.size() > 0 && s.peek() == '\"') {
                    if (s2.size() == 4 && s2.pop() == 'e' && s2.pop() == 'd' && s2.pop() == 'o' && s2.pop() == 'c' && k) {
                        return i + 1;
                    } else {
                        s.pop();
                        s2.clear();
                        if (!k) {
                            k = true;
                        }
                    }
                } else {
                    s.push(d);
                }
            } else if (d == '{' || d == '[') {
                if (s.size() > 0 && s.peek() == '\"') {
//                    s2.push(d);
                } else {
                    level++;
                    s.push(d);
                }
            } else if (d == ']' || d == '}') {
                if (s.size() > 0 && s.peek() == '\"') {
//                    s2.push(d);
                } else {
                    level--;
                    s.pop();
                }
            } else {
                if (0 == level && s2.size() < 5 && s.size() == 1 && s.peek() == '\"') {
                    s2.push(data[i]);
                }
            }
        }
        return -1;
    }


    public static Integer code(byte[] data) {
        int begin = findBegin(data);
        if (begin < 0) {
            return 200;
        }

        int index = search(data, begin + 1, data.length);
        if (index < 0) {
            return 200;
        }

        int n = num(data, 1, data.length - 1, index);
        return n;
    }


    private static int num(byte[] data, int b, int e, int begin) {
        StringBuilder sb = new StringBuilder();
        for (int i = begin; i < e; i++) {
            if (data[i] == ',' || data[i] == '}') {
                break;
            }
            if (data[i] != ' ' && data[i] != ':' && data[i] != '\"') {
                sb.append(Character.valueOf((char) data[i]));
            }
        }
        try {
            return Integer.parseInt(sb.toString().trim());
        } catch (Exception ex) {
            return 200;
        }
    }


    public static void main(String... args) {

        String str = "{\n" +
                "        \"data\": [\n" +
                "                \"}{\",\n" +
                "                \"],\",\n" +
                "                \"codexxx\",\n" +
                "                \"'code':123\",\n" +
                "                \"{\",\n" +
                "                \",\",\n" +
                "                \"\\\"\"\n" +
                "        ],\n" +
                "        \"code\": 233,\n" +
                "        \"data1\": {\n" +
                "\n" +
                "        },\n" +
                "        \"msg\": \"code\\\":1222 is wrong\"\n" +
                "}";


        String str2 = "{\"a:\":\"code\",\"code\":1223}";


        System.out.println(code(str.getBytes()));

    }


}
