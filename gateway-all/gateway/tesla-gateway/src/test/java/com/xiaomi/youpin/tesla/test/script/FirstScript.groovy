package com.xiaomi.youpin.tesla.test.script

/**
 * @Author goodjava@qq.com
 * @Date 2021/3/15 14:50
 */
class FirstScript {

    def sum(a,b) {
        a+b
    }


    def static main(def args) {
        FirstScript firstScript = new FirstScript();
        println firstScript.sum(11,22)
    }
}
