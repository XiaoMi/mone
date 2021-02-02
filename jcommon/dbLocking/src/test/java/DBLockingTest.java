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

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.xiaomi.youpin.dblocking.Locking;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

/**
 * @author Zheng Xu zheng.xucn@outlook.com
 */

public class DBLockingTest {

    private static final long VERSION_0 = 0;
    private static final long ID_1 = 1;

    private static DB db;
    private int threads = 20;
    private int N = 5;
    private int attempts = 20;

    private String driverClass = "com.mysql.jdbc.Driver";
    private Integer defaultInitialPoolSize = 10;
    private Integer defaultMaxPoolSize = 20;
    private Integer defaultMinPoolSize = 10;
    private String dataSourceUrl = "jdbc:mysql://localhost:3306/jcommonTest?characterEncoding=utf8&useSSL=false";
    private String dataSourceUserName = "root";
    private String dataSourcePasswd = "1234";

    private NutDao mockDAO;
    private NutDao realDAO;


    @Before
    public void init() throws PropertyVetoException {
        TestData data = new TestData(ID_1);
        data.setVersion(VERSION_0);
        //mock db
        db = new DB(data);

        mockDAO = createDAO(true);
        initLocalDB();
    }


    public void initLocalDB() throws PropertyVetoException {
        realDAO = createDAO(false);
        realDAO.create(TestData.class, false);
        TestData data = realDAO.fetch(TestData.class, ID_1);
        if (data == null) {
            data = new TestData(ID_1);
            data.setVersion(VERSION_0);
            realDAO.insert(data);
        } else {
            data.setVersion(VERSION_0);
            realDAO.update(data);
        }
    }

    //use a local db for testing
    @Ignore
    @Test
    public void concurrency_local_db() throws PropertyVetoException, InterruptedException {
        runTest(false, false, false, threads * N);
    }

    // use a mock database
    @Test
    public void concurrency_local_test() throws InterruptedException, PropertyVetoException {
        runTest(true, false, false, threads * N);
    }

    /**
     * use local mysql db and data object is missing the version field
     *
     * @throws PropertyVetoException
     * @throws InterruptedException
     */
    @Ignore
    @Test
    public void concurrency_local_db_MissingVersion() throws PropertyVetoException, InterruptedException {
        NutDao dao = realDAO;
        MissingVersion data = new MissingVersion(ID_1);
        data.setName("aaa");
        boolean result = Locking.write(dao, data, (dataItem) -> {
            return dataItem;
        }, 1);
        assertEquals(false, result);
    }

    /**
     * Use local mysql db and data object is a child class of BaseEntity
     *
     * @throws PropertyVetoException
     * @throws InterruptedException
     */
    @Ignore
    @Test
    public void concurrency_local_db_ExtendBase() throws PropertyVetoException, InterruptedException {
        runTest(false, true, false, threads * N);
    }

    @Test
    public void concurrency_local_test_ExtendBase() throws InterruptedException, PropertyVetoException {
        runTest(true, true, false, threads * N);
    }


    public void runTest(boolean mockDB, boolean extendBaseEntity, boolean missingVersion, long expected) throws PropertyVetoException, InterruptedException {
        Writer[] writers = new Writer[threads];
        NutDao dao = mockDB ? mockDAO : realDAO;

        for (int i = 0; i < threads; i++) {
            Object data;

            if (extendBaseEntity) {
                data = new ExtendBase(ID_1);
            } else if (!missingVersion) {
                data = new TestData(ID_1);
            } else {
                data = new MissingVersion(ID_1);
            }

            writers[i] = new Writer(mockDB, dao, data);
            writers[i].start();
        }

        for (int i = 0; i < threads; i++) {
            writers[i].join();
        }

        long currentVersion = readCurrentVersion(mockDB, dao, ID_1);
        System.out.println("currentVersion: " + currentVersion);
        assertEquals(expected, currentVersion);
    }


    public class Writer extends Thread {

        private boolean mockDB;
        private NutDao dao;
        private Object data;

        public Writer(boolean mockDB, NutDao dao, Object data) {
            this.mockDB = mockDB;
            this.dao = dao;
            this.data = data;
        }

        public void run() {
            try {
                for (int i = 0; i < N; i++) {

                    Locking.setVersion(data, readCurrentVersion(this.mockDB, dao, ID_1));
                    //模拟程序在执行业务代码
                    int random = (int) (Math.random() * 10);
                    try {
                        Thread.sleep(random);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    boolean writeResponse = Locking.write(dao, data, (dataItem) -> {

                        try {
                            long version = readCurrentVersion(this.mockDB, dao, ID_1);
                            Locking.setVersion(dataItem, version);
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }

                        return dataItem;
                    }, attempts);
                    if (!writeResponse) {
                        System.out.println("failed");
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }

        }
    }

    public long readCurrentVersion(boolean mockDB, NutDao dao, long id) {
        if (mockDB) {
            return db.readVersion();
        } else {
            TestData data = dao.fetch(TestData.class, id);
            return data.getVersion();
        }
    }

    public long parseVersion(String S) {
        int index = S.indexOf("version=");
        return Integer.parseInt(S.substring(index + 8));
    }


    public NutDao createDAO(boolean mockDB) throws PropertyVetoException {
        if (mockDB) {
            NutDao dao = Mockito.mock(NutDao.class);
            Mockito.when(dao.update(any(Object.class), any(Cnd.class))).thenAnswer(new Answer<Integer>() {
                @Override
                public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                    Object[] arguments = invocationOnMock.getArguments();
                    Object entity = arguments[0];
                    Cnd command = (Cnd) arguments[1];
                    //current version
                    long version = parseVersion(command.toString());

                    long newVersion = Locking.getVersion(entity);
                    boolean result = db.writeVersion(version, newVersion);
                    return result ? 1 : 0;
                }
            });
            return dao;
        } else {
            NutDao dao = new NutDao(masterDataSource());
            return dao;
        }
    }

    public DataSource masterDataSource() throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(driverClass);
        dataSource.setJdbcUrl(dataSourceUrl);
        dataSource.setUser(dataSourceUserName);
        dataSource.setPassword(dataSourcePasswd);
        dataSource.setInitialPoolSize(defaultInitialPoolSize);
        dataSource.setMaxPoolSize(defaultMaxPoolSize);
        dataSource.setMinPoolSize(defaultMinPoolSize);
        setDataSource(dataSource);
        return dataSource;
    }

    private void setDataSource(ComboPooledDataSource dataSource) {
        dataSource.setTestConnectionOnCheckin(true);
        dataSource.setTestConnectionOnCheckout(false);
        dataSource.setPreferredTestQuery("select 1");
        dataSource.setIdleConnectionTestPeriod(180);
    }
}
