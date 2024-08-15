package run.mone.ai.codegen.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.ToString;


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
    private FeatureGenerateType type = FeatureGenerateType.CODE_WITH_GENERATOR;

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

    private String testName = "T";

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


    /**
     * 目前使用module所在的绝对路径
     */
    @Builder.Default
    private String mybatisDaoModule = "";
    @Builder.Default
    private String mybatisXMLPath = "";
    @Builder.Default
    private String mybatisDaoPath = "";
    @Builder.Default
    private String mybatisEntityPath = "";

    /**
     * mybatis 是否覆盖现有类，默认不覆盖，如果存在现有类则跳过
     */
    @Builder.Default
    private boolean overwrite = false;

}
