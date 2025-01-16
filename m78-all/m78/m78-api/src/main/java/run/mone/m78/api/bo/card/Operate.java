package run.mone.m78.api.bo.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 操作
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Operate {

    //点击事件
    private Integer enableClickEvent;

    //点击事件类型
    private String clickEventType;

    private String clickUrl;

    private String message;

}
