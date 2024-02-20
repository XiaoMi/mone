package run.mone.m78.ip.bo.z;

import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2023/11/7 13:33
 */
@Data
public class ZResult<T> {

    private int code;

    private String message;

    private T data;

}
