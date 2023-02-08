package run.mone.docean.plugin.k8s.test.bo;

import lombok.Data;


/**
 * @author goodjava@qq.com
 */
@Data
public class MoneSpec {

    /**
     * 副本数量
     */
    private int replicas;


    /**
     * 发布的名称
     */
    private String deploymentName;

    private String namespaceName;

    /**
     * 负载
     */
    private int[] load;

    /**
     * 最大的load(系统能接受的最高load,大于等于这个值则需要扩容)
     */
    private int maxLoad;

    /**
     * 最低负载
     */
    private int minLoad;

    private long time;

    private String moneType;
}
