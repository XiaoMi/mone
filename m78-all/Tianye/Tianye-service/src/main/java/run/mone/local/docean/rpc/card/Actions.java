package run.mone.local.docean.rpc.card;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class Actions {

    private String tag;

    private List<Options> options;

    private HashMap<String,String> value;

    public static void main(String[] args) {
        Actions actions = new Actions();
        Options option = new Options();
        HashMap<String,String> text = new HashMap<>();
        text.put("tag", "plain_text");
        text.put("content", "1");
        option.setText(text);
        option.setValue("2");
        actions.setOptions(List.of(option));
        actions.setTag("actionCard");
        System.out.println(actions);
    }
}
