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

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Zheng Xu zheng.xucn@outlook.com
 */


public class DB {

    private TestData data;

    //模拟数据库锁 Database lock
    private ReentrantLock lock;

    public DB(TestData data) {
        this.data = data;
        lock = new ReentrantLock();
    }

    public long readVersion() {
        lock.lock();
        long version = this.data.getVersion();
        lock.unlock();
        return version;
    }

    public boolean writeVersion(long version, long newVersion) {
        boolean result = false;
        lock.lock();
        if (this.data.getVersion() == version) {
            this.data.setVersion(newVersion);
            result = true;
        }
        lock.unlock();
        return result;
    }

}
