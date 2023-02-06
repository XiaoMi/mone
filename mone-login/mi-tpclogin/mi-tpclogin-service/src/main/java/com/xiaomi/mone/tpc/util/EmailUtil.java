package com.xiaomi.mone.tpc.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailUtil {

    /**
     * 邮件格式校验
     * @param email
     * @return
     */
    public static boolean check(String email) {
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(email);
        return matcher.matches();
    }

}
