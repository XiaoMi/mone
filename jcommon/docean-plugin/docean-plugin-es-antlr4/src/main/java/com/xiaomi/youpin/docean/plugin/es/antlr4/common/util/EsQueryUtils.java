package com.xiaomi.youpin.docean.plugin.es.antlr4.common.util;

import com.xiaomi.youpin.docean.plugin.es.antlr4.impl.EsQueryTransfer;
import com.xiaomi.youpin.docean.plugin.es.antlr4.query.EsQueryLexer;
import com.xiaomi.youpin.docean.plugin.es.antlr4.query.EsQueryParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.elasticsearch.search.builder.SearchSourceBuilder;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/8/3 17:22
 */
public class EsQueryUtils {
    private EsQueryUtils() {

    }

    /**
     * query es search source builder
     *
     * @param code
     * @return
     */
    public static SearchSourceBuilder getSearchSourceBuilder(String code) {
        CodePointCharStream charStream = CharStreams.fromString(code);
        EsQueryLexer lexer = new EsQueryLexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        EsQueryParser parser = new EsQueryParser(tokenStream);
        ParseTree tree = parser.parse();
        EsQueryTransfer listener = new EsQueryTransfer();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, tree);
        return listener.getBuilder(tree.getChild(0));
    }

    /**
     * query es search build String
     *
     * @param code
     * @return
     */
    public static String getEsQuery(String code) {
        return getSearchSourceBuilder(code).toString();
    }
}
