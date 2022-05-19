package com.xiaomi.data.push.antlr.json;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.Map;

/**
 * @author goodjava@qq.com
 */
public class Json {

    public static String json(String str, Map<String, String> map) {
        JSONLexer lexer = new JSONLexer(new ANTLRInputStream(str));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JSONParser parser = new JSONParser(tokens);
        JSONParser.JsonContext json = parser.json();
        ParseTreeWalker walker = new ParseTreeWalker();
        JSONListenerImpl listener = new JSONListenerImpl(map);
        walker.walk(listener, json);
        return listener.getNewJson(json);
    }
}
