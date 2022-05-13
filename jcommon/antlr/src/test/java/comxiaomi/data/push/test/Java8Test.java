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

package comxiaomi.data.push.test;

import com.xiaomi.data.push.antlr.java.Java8Expr;
import com.xiaomi.data.push.antlr.java8.Java8BaseListener;
import com.xiaomi.data.push.antlr.java8.Java8Lexer;
import com.xiaomi.data.push.antlr.java8.Java8Parser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author goodjava@qq.com
 * @date 3/6/21
 */
public class Java8Test {

    @Test
    public void testjava8() throws IOException {
//        String script = new String(Files.readAllBytes(Paths.get("/Users/zzy/IdeaProjects/mione/jcommon/antlr/src/main/java/com/xiaomi/data/push/antlr/example/Example.java")));
        String script = new String(Files.readAllBytes(Paths.get("/tmp/Example.java")));
        Java8Expr java8Expr = new Java8Expr();
        java8Expr.walk(script);
        System.out.println(java8Expr.getClassPackage()  + java8Expr.getClassInfo());
        System.out.println(java8Expr.getFields());
        System.out.println(java8Expr.getMethods());
    }

}
