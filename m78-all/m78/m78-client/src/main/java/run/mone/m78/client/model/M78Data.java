package run.mone.m78.client.model;

import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2024/9/10 10:14
 */
@Data
public class M78Data<D> {

    private int code;

    private String message;

    private D data;


}
