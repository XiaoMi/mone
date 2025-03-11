package run.mone.moon.function.bo;

import lombok.Data;

@Data
public class MoonMoneTpcContext {
    private String tenant;
    private int role;
    private String account;
}
