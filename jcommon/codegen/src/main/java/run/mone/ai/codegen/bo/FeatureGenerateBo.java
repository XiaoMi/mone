package run.mone.ai.codegen.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author goodjava@qq.com, HawickMason@xiaomi.com
 * @date 7/12/24 14:10
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FeatureGenerateBo {

    @Builder.Default
    private FeatureGeneratType type = FeatureGeneratType.CODE_WITH_GENERATOR;

    @Builder.Default
    private String tableName = "";

    @Builder.Default
    private String sql = "";

    @Builder.Default
    private String jdbcUrl = "";

    @Builder.Default
    private String userName = "";

    @Builder.Default
    private String password = "";


    @Builder.Default
    private String basePackage = "";

    @Builder.Default
    private String className = "Dummy";

    @Builder.Default
    private String auth = "";

    @Builder.Default
    private String basePath = "";

    @Builder.Default
    private String serverModulePath = "";

    @Builder.Default
    private String serviceModulePath = "";

    @Builder.Default
    private String apiModulePath = "";

    @Builder.Default
    private boolean createPojo = false;

    @Builder.Default
    private boolean createVo = false;

    @Builder.Default
    private boolean createTransfer = false;

    @Builder.Default
    private boolean createService = false;

    @Builder.Default
    private boolean createTest = true;

    @Builder.Default
    private boolean createController = false;
    
}
