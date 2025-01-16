package run.mone.m78.api.bo.multiModal;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wmin
 * @date 2024/7/30
 */
@Data
public class HistoryUpdateReq implements Serializable {

    private Long id;

    private String mermaidCode;

    private String userName;

}
