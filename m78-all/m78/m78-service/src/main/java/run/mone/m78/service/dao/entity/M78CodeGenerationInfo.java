package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  实体类。
 *
 * @author zhangzhiyong
 * @since 2024-06-12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "m78_code_generation_info")
public class M78CodeGenerationInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Long ctime;

    private Long utime;

    private Integer state;

    private String projectName;

    private String className;

    private Integer codeLinesCount;

    private String methodName;

    private String username;

    /**
     * 1 根据注释生成代码
     * 2 inlay代码提示
     */
    @Builder.Default
    private int type = 1;

    private String annotation;

    /**
     * 1 idea
     * 2 vscode
     */
    @Builder.Default
    private int source = 1;

    private String pluginVersion;

    private String ip;

    private String osVersion;

    /**
     * 操作类型 (1聊天 2生成代码 3代码建议 4生成注释 5智能命名 6一键push 7单元测试 8bug_fix)
     */
    @Builder.Default
    private int action = 2;

    private String ideVersion;

    /**
     * 2024.07.23
     */
    private String day;

    /**
     * 一年中的第几周
     */
    private String weekOfYear;

    /**
     * 1-5级部门
     */
    private String tier1;
    private String tier2;
    private String tier3;
    private String tier4;
    private String tier5;

    private int totalLinesCount;

    private Long botId;

    /**
     * 代码字符数
     */
    private Integer codeCharLength;
}
