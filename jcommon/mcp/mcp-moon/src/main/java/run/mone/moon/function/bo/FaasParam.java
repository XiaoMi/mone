package run.mone.moon.function.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FaasParam implements Serializable {

    /**
     * 实例cpu配置
     */
    private String cpu;
    /**
     * 实例内存配置
     */
    private String memory;
    /**
     * 绑定的mifaas function id
     */
    private long funcID;
    /**
     * 单次执行重试次数
     */
    private Integer retries;
}
