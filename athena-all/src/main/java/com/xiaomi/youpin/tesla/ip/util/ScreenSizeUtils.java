/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.tesla.ip.util;

import java.awt.*;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/11 13:37
 */
public class ScreenSizeUtils {


    public static Dimension size() {
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dimension = new Dimension();
        dimension.setSize(screensize.width / 3, screensize.height / 3);
        return dimension;
    }

}
