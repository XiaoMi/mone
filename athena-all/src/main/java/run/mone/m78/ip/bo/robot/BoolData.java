package run.mone.m78.ip.bo.robot;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/12/15 11:08
 */
@Data
@Builder
public class BoolData implements Serializable {


    //问题内容
    private String question;


    //回答的内容(true 或者 false)
    private boolean anwser;


    @Override
    public String toString() {
        return question;
    }
}
