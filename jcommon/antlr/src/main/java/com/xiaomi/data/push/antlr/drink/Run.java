package com.xiaomi.data.push.antlr.drink;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * Created by zzy on 21/12/2017.
 */
public class Run {

    public static void main(String...args) {

        String drinkSentence = "the cup of tea";

        // Get our lexer
        DrinkLexer lexer = new DrinkLexer(new ANTLRInputStream(drinkSentence));

        // Get a list of matched tokens
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Pass the tokens to the parser
        DrinkParser parser = new DrinkParser(tokens);

        // Specify our entry point
        DrinkParser.DrinkSentenceContext drinkSentenceContext = parser.drinkSentence();

        // Walk it and attach our listener
        ParseTreeWalker walker = new ParseTreeWalker();
        DrinkListener listener = new DrinkListenerImpl();
        walker.walk(listener, drinkSentenceContext);
    }
}
