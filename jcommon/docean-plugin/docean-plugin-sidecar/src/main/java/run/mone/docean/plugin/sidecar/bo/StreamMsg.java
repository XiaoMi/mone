package run.mone.docean.plugin.sidecar.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2024/11/12 09:13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StreamMsg implements Serializable {

    private String type;

    private String content;

}
