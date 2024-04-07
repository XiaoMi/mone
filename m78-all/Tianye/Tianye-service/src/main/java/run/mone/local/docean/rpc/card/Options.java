package run.mone.local.docean.rpc.card;

import lombok.Data;

import java.util.HashMap;

@Data
public class Options {

    private HashMap<String, String> text;

    private String value;
}
