package com.xiaomi.youpin.codegen.test;

import org.junit.Test;
import run.mone.ai.codegen.util.StrUtil;

import static org.junit.Assert.assertEquals;


public class StrTest {




    @Test
    public void testToCamelCase() {
        String input = "hello world";
        String expected = "helloWorld";
        String actual = StrUtil.toCamelCase(input);
        assertEquals(expected, actual);

        input = "java programming language";
        expected = "javaProgrammingLanguage";
        actual = StrUtil.toCamelCase(input);
        assertEquals(expected, actual);

        input = "  leading and trailing spaces  ";
        expected = "leadingAndTrailingSpaces";
        actual = StrUtil.toCamelCase(input);
        assertEquals(expected, actual);

        input = "";
        expected = "";
        actual = StrUtil.toCamelCase(input);
        assertEquals(expected, actual);

        input = null;
        expected = null;
        actual = StrUtil.toCamelCase(input);
        assertEquals(expected, actual);
    }


}
