package com.xiaomi.youpin

/**
 * @author goodjava@qq.com
 * @date 3/17/21
 */
class ScriptTest {

    static class Sql {

        String sql
        List params

        static create(closure) {
            def sql = new Sql()
            sql.with closure
            sql
        }

        def query() {
            println(sql + "-->" + params)
        }
    }


    static class Ex {
        def static query(Map map) {
            String sql = map['sql']
            List params = map['params']
            println("sql:" + sql + ",params:" + params)
        }
    }


    def static main(args) {
        println("test")
        Sql.create {
            sql = "select * from t where id in ?"
            params = [12, 22]
        }.query()

        use(Ex) {
            ['sql': 'select * from t', 'params': [1, 2, 3, 4]].query()
        }

        def a = "a"
        def b = 123

        def SQL = "sql"

        def res = [SQL:"select * from t where id in ?",
                   "params":[123,344]]()

    }
}
