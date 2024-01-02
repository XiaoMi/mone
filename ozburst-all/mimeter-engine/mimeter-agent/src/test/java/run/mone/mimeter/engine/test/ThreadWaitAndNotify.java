package run.mone.mimeter.engine.test;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class ThreadWaitAndNotify {
    static final Object object=new Object();
    public static class ThreadWait extends Thread{
        @Override
        public void run(){
            synchronized (object){
                System.out.println(System.currentTimeMillis()+" A开始运行");
                try {
                    object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(System.currentTimeMillis()+" 重新获取到监视器,继续执行run方法中代码块——A结束运行");
            }
        }
    }
    public static class ThreadNotify extends Thread{
        @Override
        public void run(){
            synchronized (object){
                System.out.println(System.currentTimeMillis()+"B开始运行");
                object.notify();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(System.currentTimeMillis()+"释放监视器——B结束运行");
            }
        }
    }

    @Test
    public void test() throws InterruptedException {
        new ThreadWait().start();
        new ThreadNotify().start();

        TimeUnit.SECONDS.sleep(10);
    }
}
