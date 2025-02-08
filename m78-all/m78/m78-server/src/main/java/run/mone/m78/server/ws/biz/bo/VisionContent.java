package run.mone.m78.server.ws.biz.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/5/25 14:16
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisionContent implements Serializable {

    private String type;

    private String text;

    private Map<String, String> source;
}
