import com.xiaomi.data.push.redis.Redis
import com.xiaomi.mone.autumn.AutumnEx
import com.xiaomi.mone.autumn.ex.LoopEx
import org.junit.Test

/**
 * @Author goodjava@qq.com
 * @Date 2021/3/20 17:07
 */
class GroovyTest {

    class Sql {

        def name = "sql"

        def call() {
            return "E call"
        }

        def call(String a, Integer b) {
            return a + ":" + b
        }
    }


    /**
     * ["begin":1,"end":10](c)
     */
    @Test
    void testLoop() {
        LoopEx ex = new LoopEx()
        def m = ["begin": 1, "end": 10]
        def c = { it -> println(it) }
        ex.call(m, c)
    }


    @Test
    void test1() {
        println("test1")
    }


    @Test
    void testMap() {
        use(AutumnEx) {
            Sql sql = new Sql()
            def m = ["params": "123", "sql": "select * from t where id = ?"](sql)
            println(m)
        }
    }

    @Test
    void testMethod() {
        def sql = new Sql()
        println sql.'name'
        println sql.'call'()
        def m = 'call'
        println sql."$m"()
    }


    @Test
    void testList() {
        def list = [1, 2, 3, 4]
        println list.collect { it -> it + 1 }
        print(list as String[])
    }


    @Test
    void testMethod2() {
        Redis r = new Redis()
        List<MetaMethod> ms = r.getMetaClass().getMethods()
        ms.stream().each { println(it.getName()) }

        Object[] objs = [String.class, String.class]
        def m = r.getMetaClass().getMetaMethod("set", objs)
        println(m)


        def list = [12, "abc", 1L]
        list.each { println(it.getClass()) }

        Object[] objs1 = list.collect { it.getClass() } as Object[]
        println(objs[1])
    }


    @Test
    void testMethod3() {
        Sql sql = new Sql()
        def cmd = "call"
        def params = ["abc", 12]
        def objs = params.collect { it.getClass() } as Object[]
        def mm = sql.metaClass.getMetaMethod(cmd, objs)
        if (null != mm) {
            def res = mm.invoke(sql, params as Object[])
            println(res)
        }

    }

}
