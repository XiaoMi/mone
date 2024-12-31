package run.mone.local.docean.rpc.card;

import lombok.Data;

import java.util.List;

@Data
public class Elements {

    private String tag;

    private List<Actions> actions;
}
