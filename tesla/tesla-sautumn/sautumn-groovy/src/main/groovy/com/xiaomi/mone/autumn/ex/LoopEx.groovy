package com.xiaomi.mone.autumn.ex
/**
 * @Author goodjava@qq.com
 * @Date 2021/3/26 16:32
 * 循环增强
 */
class LoopEx {


    def call(Map m, Closure closure) {
        int begin = m.get("begin")
        int end = m.get("end")
        (begin..end).each { it -> closure.call(it) }
    }

}
