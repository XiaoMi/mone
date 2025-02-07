package run.mone.m78.api.bo.code;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReqCodeListDto implements Serializable {

    private String name;

    private Integer type;

    private Integer pageNum;

    private Integer pageSize;

}
