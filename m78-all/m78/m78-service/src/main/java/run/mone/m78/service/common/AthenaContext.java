package run.mone.m78.service.common;

import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2024/1/9 17:29
 */
@Data
public class AthenaContext {

    private String token;

    public void setZAddr(String addr) {

    }


    private static class LazyHolder {
        private static final AthenaContext ins = new AthenaContext();
    }


    public static AthenaContext ins() {
        return LazyHolder.ins;
    }

}
