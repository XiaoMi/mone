package run.mone.geth.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tran {

    private String from;
    private String to;
    private String value;

}
