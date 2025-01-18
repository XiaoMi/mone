package run.mone.m78.api.bo.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Content {

    //参数类型：expression、text
    private String type;

    //expression->${variable_name}
    private String value;

    private Integer total;

    private Integer score;

}
