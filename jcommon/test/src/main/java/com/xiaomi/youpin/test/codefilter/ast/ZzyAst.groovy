package com.xiaomi.youpin.test.codefilter.ast

import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * @author goodjava@qq.com
 * @date 3/15/21
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.FIELD, ElementType.TYPE])
@GroovyASTTransformationClass("com.xiaomi.youpin.test.codefilter.ast.MyASTTransformation")
@interface ZzyAst {

}