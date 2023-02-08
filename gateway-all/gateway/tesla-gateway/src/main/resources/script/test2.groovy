import com.google.gson.Gson

//前置执行
def before(apiInfo, req) {
//    print(apiInfo, req)
    //可以修改入参
    apiInfo.id = 11111
    System.out.println(apiInfo)
    log.info("----------->test log")
}


//后置执行
def after(apiInfo, req, res) {
    //可以修改返回结果
    gson = new Gson()
    Map m = gson.fromJson(res, Map.class)
    m.put("msg", m.get("msg") + " modify")
    gson.toJson(m)
}


