package run.mone.m78.service.bo.code;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2024/3/9 21:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Code implements Serializable {

    private String language;

    private String name;

    private List<Param> params;

    private String code;

    private List<Param> outs;

}
