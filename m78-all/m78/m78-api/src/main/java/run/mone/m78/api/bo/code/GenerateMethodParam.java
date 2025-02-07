package run.mone.m78.api.bo.code;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dp
 * @date 2024/1/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateMethodParam {

    private String uuid;

    private String languageType;

    private String comment;

    private String user;
}
