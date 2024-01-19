package run.mone.ultraman.bo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/11/5 22:46
 */
@Data
@Builder
public class CodeReq {

    private String projectName;

    private String moduleName;

    private String className;

    private String methodName;

    private String code;

    private List<String> codeList;

    private String data;

    //需求
    private String requirement;

    //返回几条记录
    @Builder.Default
    private int limit = 1;

    //文榜需要用到的一个id,随便起就可以
    @Builder.Default
    private Long knowledgeBaseId = 1038L;

    private List<String> fileTypeList;

}
