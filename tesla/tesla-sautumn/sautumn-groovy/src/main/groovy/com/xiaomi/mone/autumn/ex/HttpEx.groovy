package com.xiaomi.mone.autumn.ex

/**
 * @Author dingpei* @Date 2021/3/22 15:22
 */
class HttpEx {

    def call(Map m, def http) {
        String cmd = m.get("cmd")
        String url = m.get("url")
        if ("post".equals(cmd)) {
            Map<String, Object> params = m.get("param")
            Integer timeout = m.get("timeout")
            return http.post(url, params, timeout)
        }
        if ("get".equals(cmd)) {
            return http.get(url)
        }
        return "dont support this http cmd"
    }

}
