///*
// *  Copyright 2020 Xiaomi
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//package com.xiaomi.youpin.tesla.test;
//
//import com.xiaomi.youpin.gateway.common.Utils;
//import com.xiaomi.youpin.gateway.service.UserService;
//import org.apache.thrift.TException;
//import org.junit.Test;
//
///**
// * @author 丁春秋
// */
//public class CookieTest {
//    @Test
//    public void test() throws TException {
//        String cookie = "mjclient=PC; UM_distinctid=168ea9f8ea3390-080ae39f79516-133a6850-13c680-168ea9f8ea4a0f; youpindistinct_id=168ea9f8b9f-0223fec90cd9b5-22dd; Hm_lvt_3d0145da4163eae34eb5e5c70dc07d97=1551078906,1551941860; youpin_sessionid=1554710675961_169fbfa41f9526-06cc746b1812f5-12306d51; youpindistinct_id=168ea9f8b9f-0223fec90cd9b5-22dd; mjclient=PC; Hm_lvt_025702dcecee57b18ed6fb366754c1b8=1554710676,1554725814,1554726191,1555397027; CNZZDATA1267968936=790728077-1550122351-null%7C1555480799; b_auth=mijiayoupin; serviceToken=Br6K/41oaqvv22kVo/3iJP953+hdCMjcyuhyI3D5OILrujJFk014pxQm/uL4jAHGoA5MfFNDQMgxOypb2w3w0VwgTrA2APAl/T/9Ykpy/LLjjSDvjXcey8B/uYfwc/6vHtYVZlrTF/Co1DhhA6hymQ==; cUserId=i4drA6OsQilob2e3AEsDME7eHCM; exchangeToken=Meix5Z6_gMcE3ImSxVx3Lz24fDebBDU7NLasB_TbO-LXp0c1SwUEbIICzn5zCdHGW-voK_57lL1mtSA91zR0cTjQeImHlSop4u4WPgqY2gECUkw0EdQj3tODqe9fz7y-G7DWgxH1v2NhDL7zZ9PA7CPyZJYF37V5gBg9g7oR-zZX9iUWG7xFL8bNt6FiNgRurFW3-8zMfYcWJPdKKEadAq5eXqxq09O88Lk101r7Jnj6NW4Nw6sgHx9QzpV4OF_eUzTnH5te__jNKIUPmvH69g==; youpin_sessionid=16a2a122040-04c4ebae0dfcea-22e0; Hm_lpvt_025702dcecee57b18ed6fb366754c1b8=1555483992";
//        UserService userService = new UserService();
//        long uid = userService.getUidFromCookie(cookie);
//        System.out.printf(String.valueOf(uid));
//    }
//
//
//    @Test
//    public void testA() {
//        System.out.println("a");
//    }
//
//    @Test
//    public void testB() {
//        String cacheRoutePath = "/tmp/tesla/";
//        String fileName = "api_route.cache";
//        Utils.writeFile(cacheRoutePath, fileName, "aaa");
//    }
//}
