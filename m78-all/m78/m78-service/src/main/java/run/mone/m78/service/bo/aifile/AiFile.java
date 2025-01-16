package run.mone.m78.service.bo.aifile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author goodjava@qq.com
 * @date 2024/8/9 12:35
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiFile {

    private String content;

    private int id;

    private String name;

}
