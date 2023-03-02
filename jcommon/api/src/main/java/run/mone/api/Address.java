package run.mone.api;

import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/3/1 09:36
 */
@Data
public class Address implements Serializable {

    private String ip;

    private int port;

}
