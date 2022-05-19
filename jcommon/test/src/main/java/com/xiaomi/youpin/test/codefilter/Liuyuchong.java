package com.xiaomi.youpin.test.codefilter;


import java.util.ArrayList;

public class Liuyuchong {

    public static Integer code(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return 200;
        }

        byte space = " ".getBytes()[0];
        int beginIndex = 0;

        //�˳�ǰ��Ŀո�
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] != space) {
                beginIndex = i;
                break;
            }
        }

        byte bracket1Start = "{".getBytes()[0];
        byte bracket1End = "}".getBytes()[0];
        byte bracket2Start = "[".getBytes()[0];
        byte bracket2End = "]".getBytes()[0];
        if (bytes[beginIndex] != bracket1Start) {
            return 200;
        }

        byte[] code = "code".getBytes();
        byte escape = "\\".getBytes()[0];
        byte quota = "\"".getBytes()[0];
        boolean quotaFlag = false;
        int quotaIndex=-1;
        int bracket1sCount = 1;
        int bracket2sCount = 0;
        for (int i = beginIndex + 1; i < bytes.length; i++) {
            if (bytes[i] == escape) {
                i++;
                continue;
            }

            if (bytes[i] == quota) {
                quotaFlag = !quotaFlag;
                quotaIndex = i;
            }

            if (bytes[i] == bracket2End && !quotaFlag) {
                bracket2sCount--;
                if (bracket2sCount < 0) {
                    return 200;
                }
            } else if (bytes[i] == bracket2Start && !quotaFlag) {
                bracket2sCount++;
            }

            if (bracket2sCount > 0 && quotaFlag) {
                continue;
            }
            if (bracket1sCount == 1) {
                if (bytes[i] == bracket1End && !quotaFlag) {
                    return 200;
                } else if (bytes[i] == bracket1Start && !quotaFlag) {
                    bracket1sCount++;
                } else if (bytes[i] == code[0]&&quotaFlag&&i==quotaIndex+1) {
                    if (checkCode(bytes, i)) {
                        return getCode(bytes, i + code.length + 1);
                    }
                }
            } else {
                if (bytes[i] == bracket1Start && !quotaFlag) {
                    bracket1sCount++;
                } else if (bytes[i] == bracket1End && !quotaFlag) {
                    bracket1sCount--;
                }
            }
        }
        return 200;
    }

    /**
     * ��indexλ�ÿ�ʼУ������ֽڴ��Ƿ���code���ֽڴ�
     */
    private static boolean checkCode(byte[] bytes, int index) {
        byte[] code = "code".getBytes();
        for (int i = 0; i < code.length; i++) {
            if (bytes[index + i] != code[i]) {
                return false;
            }
        }
        byte quota = "\"".getBytes()[0];
        if (quota != bytes[index+code.length]) {
            return false;
        }
        return true;
    }

    private static int getCode(byte[] bytes, int start) {
        ArrayList<Byte> result = new ArrayList<>();
        boolean flag = false;
        byte colon = ":".getBytes()[0];
        byte comma = ",".getBytes()[0];
        byte space = " ".getBytes()[0];
        byte end = "}".getBytes()[0];
        byte quota = "\"".getBytes()[0];
        for (int i = start; i < bytes.length; i++) {
            if (flag) {
                if (bytes[i] == comma || bytes[i] == space || bytes[i] == end) {
                    try {
                        return Integer.valueOf(new String(listToByteArray(result)));
                    } catch (Exception e) {
                        return 200;
                    }
                } else {
                    result.add(bytes[i]);
                }
            }else if (bytes[i] != space&&bytes[i]!=colon&&bytes[i]!=quota) {
                    flag=true;
                    result.add(bytes[i]);
            }
        }
        return 200;
    }

    private static byte[] listToByteArray(ArrayList<Byte> result) {
        byte[] bytes = new byte[result.size()];
        for (int i = 0; i < result.size(); i++) {
            bytes[i] = result.get(i);
        }
        return bytes;
    }

}


