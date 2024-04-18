package run.mone.local.docean.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-02-27 14:42
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageProxy implements Serializable {

    private String role;

    private String content;

    private String promptName;

    private Map<String, String> params;
}
