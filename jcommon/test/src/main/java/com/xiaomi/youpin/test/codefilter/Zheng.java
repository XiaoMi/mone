package com.xiaomi.youpin.test.codefilter;

public class Zheng {

    static final byte[] specials = {(byte) '{', (byte) '}', (byte) '[', (byte) ']', (byte) '\'', (byte) '\"'};
    static final byte[] acceptableBytes = {(byte) ',', (byte) ' ', (byte) '\'', (byte) '\"', (byte) '}', (byte) '\n', (byte) '\t'};
    static final byte[] pattern = {99, 111, 100, 101};

    public static int code(byte[] A) {
        if (A == null || A.length == 0) {
            return 200;
        }
        // code =99 111 100 101

        int startIndex = startIndex(A);
        int endIndex = endIndex(A);
        if (!isJson(A, startIndex, endIndex)) {
            return 200;
        }
        startIndex++;
        endIndex--;

        boolean colon = false;
        int levels = 0;
        for (int i = startIndex; i <= (endIndex + 1) - pattern.length; i++) {

            byte b = A[i];

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
                    while (!(A[i] == b && (i - 1 < 0 || A[i - 1] != (byte) '\\'))) {
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

            if (colon && A[i] == (byte) ',') {
                colon = false;
                continue;
            }

            int nextI = i;
            if (!colon && isQuotation(b)) {
                //处理有引号的key
                int j = i + 1;
                //找出匹配的引号
                while (!(A[j] == b && (j - 1 < 0 || A[j - 1] != (byte) '\\'))) {
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

        return 200;
    }

    /**
     * 处理key
     *
     * @param index
     * @param A
     * @return
     */
    private static int processKey(int index, byte[] A) {
        boolean match = patternSearch(index, A);

        if (match && isValidKey(index, A)) {
            int code = parseCode(A, index + pattern.length);
            if (code != -1) {
                return code;
            }
            return 200;
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
    private static boolean patternSearch(int i, byte[] A) {
        for (int j = 0; j < pattern.length; j++) {
            if (A[i + j] != pattern[j]) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidKey(int i, byte[] A) {
        int index = i - 1;
        return index >= 0 && (A[index] == (byte) ',' || A[index] == (byte) '{' || A[index] == (byte) ' ' || A[index] == (byte) '\'' || A[index] == (byte) '\"');
    }

    private static int parseCode(byte[] A, int start) {
        final int DEFAULT_CODE = -1;
        int i = start;

        //找冒号
        while (i < A.length && A[i] != (byte) ':') {
            if (!(A[i] == (byte) ' ' || A[i] == (byte) '\'' || A[i] == (byte) '\"' || A[i] == (byte) '\t' || A[i] == (byte) '\n')) {
                return DEFAULT_CODE;
            }
            i++;
        }
        i++;

        //找数字的开始
        while (i < A.length && !isNumber(A[i])) {
            if (!(A[i] == (byte) ' ' || A[i] == (byte) '\'' || A[i] == (byte) '\"' || A[i] == (byte) '\n' || A[i] == (byte) '\t')) {
                return DEFAULT_CODE;
            }
            i++;
        }

        //Integer.parseInt
        int code = 0;
        boolean hasNumber = false;
        while (i < A.length && isNumber(A[i])) {
            code = code * 10 + toDigit(A[i]);
            i++;
            hasNumber = true;
        }

        return validateCode(hasNumber, i, A, code);
    }

    private static int validateCode(boolean hasNumber, int i, byte[] A, int code) {
        if (!hasNumber) {
            return -1;
        }

        if (i < A.length) {
            for (byte b : acceptableBytes) {
                if (A[i] == b) {
                    if (code < 0) {
                        //检测 integer overflow
                        code = 200;
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

    private static boolean isJson(byte[] A, int startIndex, int endIndex) {
        if (startIndex == A.length || endIndex < 0) {
            return false;
        }

        byte left = (byte) '{';
        byte right = (byte) '}';
        return A[startIndex] == left && A[endIndex] == right;
    }

    private static int startIndex(byte[] A) {
        int i = 0;
        while (i < A.length && A[i] != (byte) '{' && A[i] != (byte) '[') {
            i++;
        }
        return i;
    }

    private static int endIndex(byte[] A) {
        int i = A.length - 1;
        while (i >= 0 && A[i] != (byte) '}' && A[i] != (byte) ']') {
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