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
