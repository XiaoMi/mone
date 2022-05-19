package com.xiaomi.data.push.uds.codes;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author goodjava@qq.com
 * @date 1/23/21
 */
@Slf4j
public class HessianCodes implements ICodes {
    @Override
    public <T> T decode(byte[] data, Type type) {
        ByteArrayInputStream is = new ByteArrayInputStream(data);
        HessianInput hi = new HessianInput(is);
        try {
            return (T) hi.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                hi.close();
                is.close();
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }
    }

    @Override
    public <T> byte[] encode(T t) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HessianOutput ho = new HessianOutput(os);
        try {
            ho.writeObject(t);
            return os.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                ho.close();
                os.close();
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }
    }

    @Override
    public byte type() {
        return 1;
    }
}
