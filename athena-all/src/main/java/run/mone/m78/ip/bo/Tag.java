package run.mone.m78.ip.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/5/27 14:45
 */
@Data
public class Tag implements Serializable {


    private int id;

    private String name;

    private String description;


}
