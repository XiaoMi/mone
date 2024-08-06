package run.mone.ultraman.test;

import com.xiaomi.youpin.tesla.ip.util.MarkdownFilter;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 2024/1/31 11:30
 */
public class MarkdownFilterTest {

    @Test
    public void testFilter() {
        MarkdownFilter filter = new MarkdownFilter(System.out::print);

        filter.accept("Here is some text ");
        filter.accept("and here is a code block: ```");
        filter.accept("java");
        filter.accept("System.out.println(\"Hello, world!\");");
        filter.accept("``` and now we're back to text.");
    }

    @Test
    public void testFilter2() {
        MarkdownFilter filter = new MarkdownFilter(System.out::print);
        filter.accept("");
        filter.accept("```");
        filter.accept("go");
        filter.accept("public void p(){");
        filter.accept("System.out.println(\"Hello, world! ```\");");
        filter.accept("}");
        filter.accept("```");
        filter.accept("\nend");
        System.out.println("");
    }

    @Test
    public void testFilter3() {
        MarkdownFilter filter = new MarkdownFilter(System.out::print);
        filter.accept("public void p(){");
        filter.accept("System.out.println(\"Hello, world! ```\");");
        filter.accept("}");
        System.out.println("");
    }
}
