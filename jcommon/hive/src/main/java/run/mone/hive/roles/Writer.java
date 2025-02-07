
package run.mone.hive.roles;

import lombok.Data;
import run.mone.hive.schema.Message;

/**
 * 作家
 */
@Data
public class Writer extends Role {

    public Writer(String name) {
        super(name,"你是一名优秀的中文作家,我给你一个题目,你通过你自己的规划写出来一篇完美的文章.");
    }


    @Override
    public Message processMessage(Message message) {
        message.setSentFrom(this.name);
        return message;
    }
}
