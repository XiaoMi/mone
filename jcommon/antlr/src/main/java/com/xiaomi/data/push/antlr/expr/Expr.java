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

package com.xiaomi.data.push.antlr.expr;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * @author zzy
 * @date 21/12/2017
 *
 * 作用:通过表达式从类中提取值
 */
public class Expr {

    public static String result(Object result, String script) {
        return invoke(result, script).toString();
    }


    public static Object invoke(Object result, String script) {
        ExprLexer lexer = new ExprLexer(new ANTLRInputStream(script));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ExprParser parser = new ExprParser(tokens);
        ExprParser.ProgContext prog = parser.prog();
        ParseTreeWalker walker = new ParseTreeWalker();
        ExprListenerImpl listener = new ExprListenerImpl("result", result);
        walker.walk(listener, prog);
        return listener.cal();
    }


    public static Object params(Object result, String script) {
        ExprLexer lexer = new ExprLexer(new ANTLRInputStream(script));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ExprParser parser = new ExprParser(tokens);
        ExprParser.ProgContext prog = parser.prog();
        ParseTreeWalker walker = new ParseTreeWalker();
        ExprListenerImpl listener = new ExprListenerImpl("params", result);
        walker.walk(listener, prog);
        return listener.cal();
    }


    public static String version() {
        return "0.0.1:2019-11-04";
    }

}
