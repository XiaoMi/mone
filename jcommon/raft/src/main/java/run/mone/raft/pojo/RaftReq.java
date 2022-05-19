package run.mone.raft.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2022/4/17
 */
@Data
public class RaftReq implements Serializable {

    public static final String BEAT = "beat";

    public static final String VOTE = "vote";

    public static final String PEER = "peer";

    private String cmd;

    /**
     * 投票的内容
     */
    private String vote;

    /**
     * 心跳的内容
     */
    private String beat;


    public static final String CLIENT = "client";
    public static final String DISTRO_FILTER = "distroFilter";

    private String from = CLIENT;

}
