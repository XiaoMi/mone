package run.mone.m78.service.bo.code;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2024/3/9 21:58
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Param {

    private String name;

    private String type;

    private List<Param> schema;

    private String originalType;

}
