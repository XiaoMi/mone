package com.xiaomi.data.push.hessian;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author goodjava@qq.com
 */
public class HessianUtils {


    private HessianUtils() {

    }

    public static byte[] write(Object obj) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HessianOutput ho = new HessianOutput(os);
        try {
            ho.writeObject(obj);
            return os.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                ho.close();
                os.close();
            } catch (Exception ex) {
                //ignore
            }
        }
    }


    public static Object read(byte[] data) {
        ByteArrayInputStream is = new ByteArrayInputStream(data);
        HessianInput hi = new HessianInput(is);
        try {
            return hi.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                hi.close();
                is.close();
            } catch (Exception ex) {
                //ignore
            }
        }
    }
}
