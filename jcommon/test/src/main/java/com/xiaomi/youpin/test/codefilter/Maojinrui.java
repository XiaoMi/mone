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
