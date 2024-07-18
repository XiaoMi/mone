package com.xiaomi.youpin.tesla.ip.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
public class M78CodeGenerationInfo implements Serializable {

    private static final long serialVersionUID = 1L;

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

    private String systemVersion;

    /**
     * 操作类型 (1聊天 2生成代码 3代码建议 4生成注释 5智能命名 6一键push 7单元测试 8bug_fix)
     */
    @Builder.Default
    private int action = 2;

    private String ideVersion;
}
