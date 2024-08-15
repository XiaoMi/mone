package run.mone.ai.codegen.bo;

import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class MybatisGeneratorResult {
    /**
     * 生成的Java Entity类列表
     */
    private String entity;

    /**
     * 生成的Java mapper类列表
     */
    private String mapper;
}
