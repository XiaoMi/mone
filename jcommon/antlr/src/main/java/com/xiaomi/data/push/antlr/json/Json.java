/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
