package run.mone.m78.client.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/6/9 14:32
 */
@Data
@Builder
public class M78Message implements Serializable {

    private M78MessageType type;

    private String message;

    @Builder.Default
    private M78MessageCategory category = M78MessageCategory.bot;

    private String id;

    private String text;

    private String projectName;

    //代表是否是编码(```code```)
    @Builder.Default
    private boolean code = true;

}
