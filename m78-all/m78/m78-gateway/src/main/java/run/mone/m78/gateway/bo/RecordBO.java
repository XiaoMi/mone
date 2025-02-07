package run.mone.m78.gateway.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class RecordBO implements Serializable {

    private String role;
    private String content;
}
