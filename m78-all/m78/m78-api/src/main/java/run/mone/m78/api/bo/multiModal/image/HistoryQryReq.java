package run.mone.m78.api.bo.multiModal.image;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wmin
 * @date 2024/7/30
 */
@Data
public class HistoryQryReq implements Serializable {

    private String userName;

    private Integer runStatus;

    private Integer type;

    private Integer pageNum;

    private Integer pageSize;

}
