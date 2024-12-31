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

package run.mone.ultraman.test;

import com.xiaomi.youpin.tesla.ip.common.VersionUtil;
import org.junit.Test;

/**
 * @author shanwb
 * @date 2024-06-18
 */
public class VersionUtilTest {
    @Test
    public void testCompareVersions() {
        System.out.println(VersionUtil.compareVersions("2024.06.18.1", "2024.06.18.1") == 0);
        System.out.println(VersionUtil.compareVersions("2024.7.18.1", "2024.06.18.1") > 0);
        System.out.println(VersionUtil.compareVersions("2024.06.18.2", "2024.06.18.1") > 0);
        System.out.println(VersionUtil.compareVersions("2024.06.18.1", "2024.06.18.2") < 0);
        System.out.println(VersionUtil.compareVersions("2024.06.19.1", "2024.06.18.1") > 0);
        System.out.println(VersionUtil.compareVersions("2024.06.18.1", "2024.06.19.1") < 0);
        System.out.println(VersionUtil.compareVersions("2025.06.18.1", "2024.06.18.1") > 0);
        System.out.println(VersionUtil.compareVersions("2024.06.18.1", "2025.06.18.1") < 0);
        System.out.println(VersionUtil.compareVersions("2024.07.18.1", "2024.06.18.1") > 0);
        System.out.println(VersionUtil.compareVersions("2024.06.18.1", "2024.07.18.1") < 0);
        System.out.println(VersionUtil.compareVersions("2024.06.18.1", "2024.06.18") > 0);
        System.out.println(VersionUtil.compareVersions("2024.06.18", "2024.06.18.1") < 0);
    }

}
