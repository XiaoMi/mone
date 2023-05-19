package run.mone.openai;

import com.unfbx.chatgpt.entity.chat.Message;
import lombok.Data;
import run.mone.openai.listener.AskListener;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/5/19 09:19
 */
@Data
public class Ask extends Message implements Serializable {

    private AskListener askListener;

    /**
     * 这个问题是否被解答了
     */
    private boolean finish;

    /**
     * 流式回答
     */
    private boolean stream;


}
