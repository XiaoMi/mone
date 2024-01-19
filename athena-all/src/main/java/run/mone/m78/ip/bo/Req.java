package run.mone.m78.ip.bo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/6/17 09:40
 */
@Data
@Builder
public class Req implements Serializable {

    private int code;

    private String message;

    private String sound;

}
