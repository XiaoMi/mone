package com.xiaomi.mone.tpc.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @project: mi-iam
 * @author: zgf1
 * @date: 2022/3/28 15:56
 */
public class ListUtil {

    public static <T> List<T> list(T... args) {
        if (args == null || args.length == 0) {
            return new ArrayList<>(0);
        }
        return Arrays.stream(args).collect(Collectors.toList());
    }
}
