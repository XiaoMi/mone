package com.xiaomi.data.push.uds.codes;

import com.xiaomi.data.push.common.UdsException;

/**
 * @author goodjava@qq.com
 * @date 1/22/21
 */
public class CodesFactory {

    private static GsonCodes gsonCodes = new GsonCodes();

    private static HessianCodes hessianCodes = new HessianCodes();

    private static BytesCodes bytesCodes = new BytesCodes();

    public static ICodes getCodes(byte id) {
        if (id == gsonCodes.type()) {
            return gsonCodes;
        }

        if (id == hessianCodes.type()) {
            return hessianCodes;
        }

        if (id == bytesCodes.type()) {
            return bytesCodes;
        }

        throw new UdsException("type error");
    }

}
