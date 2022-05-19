package run.mone.raft.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2022/4/17
 */
@Data
public class RaftRes implements Serializable {

    private String cmd;

    private String res;

    private int code;

    private String message;

}
