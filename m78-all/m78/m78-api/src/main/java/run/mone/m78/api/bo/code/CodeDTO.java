package run.mone.m78.api.bo.code;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/11/24 14:45
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CodeDTO implements Serializable {

    private static final long serialVersionUID = 2437468757890536436L;

    private Long id;

    private CodeContentDTO code;

    private String name;

    private String desc;

    private Integer type;

    private String model;

    private String creator;

    private Long ctime;

    private Long utime;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class CodeContentDTO implements Serializable {

        private static final long serialVersionUID = 256698131566645853L;

        private String name;

        private String language;

        private List<CodeParamDTO> params;

        private String code;

        private List<CodeParamDTO> outs;

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class CodeParamDTO implements Serializable {

        private static final long serialVersionUID = -7646951138269813280L;

        private String name;

        private String type;

    }

}
