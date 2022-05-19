package com.xiaomi.data.push.common;

import com.xiaomi.data.push.uds.codes.GsonCodes;
import com.xiaomi.data.push.uds.codes.HessianCodes;
import com.xiaomi.youpin.docean.exception.DoceanException;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 1/8/21
 */
@Slf4j
public abstract class CovertUtils {

    public static Object[] convert(byte type, Class[] classes, byte[][] params) {
        if (0 == type) {
            return convert(classes, params);
        }
        if (1 == type) {
            return convertHessian(classes, params);
        }
        throw new DoceanException("type error");
    }


    /**
     * gson转换
     *
     * @param classes
     * @param params
     * @return
     */
    public static Object[] convert(Class[] classes, byte[][] params) {
        return IntStream.range(0, classes.length).mapToObj(i -> new GsonCodes().decode(params[i], classes[i])).toArray();
    }

    /**
     * hessian转换
     *
     * @param classes
     * @param params
     * @return
     */
    public static Object[] convertHessian(Class[] classes, byte[][] params) {
        return IntStream.range(0, classes.length).mapToObj(i -> new HessianCodes().decode(params[i], classes[i])).toArray();
    }


}
