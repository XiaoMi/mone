package run.mone.m78.api.bo.code;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/12/24 09:45
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CodeReq {

    private String comment;

    private String model;

    @Builder.Default
    private Boolean saveDB = true;

}
