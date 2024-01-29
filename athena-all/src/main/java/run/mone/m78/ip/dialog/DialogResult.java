package run.mone.m78.ip.dialog;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/5/15 16:00
 */
@Data
public class DialogResult implements Serializable {

    private int code;

    private String cmd;

    private String message;

    private Map<String,String> data = new HashMap<>();


}
