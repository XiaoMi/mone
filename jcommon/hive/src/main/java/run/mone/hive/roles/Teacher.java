package run.mone.hive.roles;

import lombok.Data;
import run.mone.hive.schema.Message;

/**
 * 教师
 */
@Data
public class Teacher extends Role {
    public Teacher(String name) {
        super(name, "你是一名优秀的教师,我给你一个主题,你能够制定出一个完整的教学计划.");
    }

    @Override
    public Message processMessage(Message message) {
        message.setSentFrom(this.name);
        return message;
    }

}