package run.mone.m78.api.bo.recommendCarousel;

import lombok.Data;

import java.io.Serializable;

@Data
public class ListQryReq implements Serializable {

    private String title;

    private Integer type;

    private Integer displayStatus;

    private Integer pageNum;

    private Integer pageSize;

}
