package com.xiaomi.youpin.docean.common;

import net.sf.cglib.beans.BeanCopier;

/**
 * @author goodjava@qq.com
 * @date 2022/11/27 10:31
 */
public class BeanUtils {

    public static void copy(Object source, Object target) {
        BeanCopier copier = BeanCopier.create(source.getClass(), target.getClass(), false);
        copier.copy(source, target, null);
    }

}
