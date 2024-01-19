package run.mone.m78.ip.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/4/27 09:26
 *
 * 插件的一些服务器信息
 */
@Data
public class ServerInfo implements Serializable {

    private String host;

    private int port;

    private List<String> projectList;

    private List<String> moduleList;

    private List<PromptInfo> promptList;

    private String gptModel;

    /**
     * ai proxy debug 模式
     */
    private boolean aiProxyDebug;


    /**
     * 是否使用local模式(所有问答直走本地)
     */
    private boolean local;

    //是否开启多模态
    private boolean vision;

    /**
     * 是否香后端发送append_msg
     */
    private boolean send;

}
