//package com.xiaomi.youpin.test.codefilter
//
//import com.xiaomi.youpin.test.codefilter.ast.ZzyAst
//import groovy.transform.ToString
//
///**
// * @author goodjava@qq.com
// * @date 3/12/21
// */
//class GroovyRun {
//
//    static class E {
//
//    }
//
//    def static te() {
//        def e = new E()
//        println(e.getName())
//    }
//
//    def static meta() {
//        String.metaClass.'static'.name = {
//            return "string name"
//        }
//
//        String.metaClass.print = {
//            println("gogogo")
//        }
//        "123".print()
//        println(String.name())
//    }
//
//    static class ExMethod {
//        def static log(String message) {
//            println(message)
//        }
//
//        def static log(String message, String param) {
//            println(message + "--->" + param)
//        }
//
//        def static sql(String message, String cmd, def ... params) {
//            println("sql," + message + params)
//            [1, 2, 3, 4]
//        }
//
//        def static redis(String value, String cmd, String... params) {
//            println("redis," + value + "," + cmd + "," + params)
//        }
//
//        def static rpc(List list) {
//            println(list)
//        }
//
//        def static log(Map<String, String> map) {
//            map.each { it -> println it.getKey() }
//        }
//    }
//
//    def static exMethod() {
//        use(ExMethod) {
//            "1234".log()
//            def m = [:]
//            m["name"] = "zzy"
//            m.log()
//            "1234".log("go")
//            //sql 操作
//            "select * from table1 where id =? and name=?".sql("query", 1, "zzy")
//            //redis 操作
//            "123".redis("set", "name")
//            //dubbo 操作
//            ["DubboService", "call", ["a", "b"]].rpc()
//        }
//    }
//
//    static class MyMixin {
//        def Get(String str) {
//            return str + "mixin"
//        }
//    }
//
//    def static mixin() {
//        String.mixin(MyMixin)
//        println "abc".Get("vv")
//    }
//
//    def static closure(Closure<String> closure, value) {
//        closure.call(value)
//    }
//
//    def static c(Closure<String> c) {
//        c.call()
//    }
//
//    def static d = {
//        println("d")
//        return "d"
//    }
//
//    def static sum(a, b) {
//        return a + b;
//    }
//
//    def static list() {
//        def l = [1, 2, 3]
//        l.each { it -> println(it) }
//    }
//
//    def static map() {
//        def m = [:]
//        m["name"] = "zzy"
//        m["id"] = 12
//        m.each { it -> println(it.getKey() + "-->" + it.getValue()) }
//    }
//
//    @ToString
//    static class EE {
//        int i = 1
//        String s = "2"
//    }
//
//    def static ee() {
//        println(new EE())
//    }
//
//    @ZzyAst
//    static class FF {
//        int i = 12
//    }
//
//    def static ast() {
//        def ff = new FF()
//    }
//
//    def static ast2() {
//        def parent = GroovyRun.classLoader
//        def loader = new GroovyClassLoader(parent)
////        def gclass = loader.parseClass(new File("/tmp/Person.groovy"))
////        def gclass = loader.parseClass(new File("/tmp/Person.groovy"))
//        def gclass = loader.parseClass(new File("/tmp/groovy/Person.groovy"))
//
//        def obj = gclass.newInstance()
////        gclass.getDeclaredFields().each { it -> println(it) }
////        def field = gclass.getDeclaredField("abc")
////        field.setAccessible(true)
////        field.set(obj, "123")
////        println(field.get(obj))
//
//
//        def m = gclass.getDeclaredMethod("sum")
//        m.invoke(obj)
//
//
//    }
//
//
//    static def main(def args) {
////        println sum(11, 22)
////        list()
////        map()
////        te()
////        closure({ it -> println(it) }, 100)
////        meta()
////        exMethod()
////        mixin()
////        ee()
////        ast()
//        ast2()
////        println("abc")
////        c d
//    }
//
//}
