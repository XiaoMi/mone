package run.mone.m78.gateway.bo;

import lombok.Data;

import java.util.List;

@Data
public class ResponseBodyBO {

    private int status;

    private String answer;

    private String markdownAnswer;

    private List<RelationBO> relations;
}
