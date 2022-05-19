package com.xiaomi.data.push.antlr.csv;

import com.google.common.collect.Lists;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by zhangzhiyong
 */
public class Run {

    public static void main(String... args) {

        String source =
            "aaa,bbb,ccc\n" + "kkkk,eee,fff\n";

        CSVLexer lexer = new CSVLexer(new ANTLRInputStream(source));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CSVParser parser = new CSVParser(tokens);

        List<List<String>>list = Lists.newArrayList();

        CSVParser.FileContext file = parser.file();

        CSVParser.HdrContext hdr = file.hdr();
        Stream<CSVParser.FieldContext> hds = hdr.row().field().stream();
        List<String> head = Lists.newArrayList();
        hds.forEach(it->{
            head.add(it.TEXT().toString());
        });
        list.add(head);

        List<CSVParser.RowContext> rows = file.row();
        rows.stream().forEach(it -> {
            List<String>tmp = Lists.newArrayList();
            it.field().stream().forEach(it2 -> {
                tmp.add(it2.TEXT().toString()
                );
            });
            list.add(tmp);
        });

        System.out.println(list);

    }
}
