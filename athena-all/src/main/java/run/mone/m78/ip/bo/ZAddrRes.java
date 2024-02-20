package run.mone.m78.ip.bo;

import lombok.Data;

import java.util.List;

/**
 * @author caobaoyu
 * @description:
 * @date 2023-06-07 14:40
 */
@Data
public class ZAddrRes {

    private String addr;

    private String version;

    private String athenaConfig;

    /**
     * athena dashboard的server地址(前端那个服务)
     */
    private String athenaDashServer;

    /**
     * 支持哪些模型
     */
    private List<ModelRes> models;

    private List<ModelRes> modelsV2;


}
