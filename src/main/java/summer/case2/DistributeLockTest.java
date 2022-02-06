package summer.case2;

import java.util.concurrent.*;

/**
 * @author summer
 * @title: DistributeLockTest
 * @projectName Zookeeper
 * @description: TODO
 * @date 2022-02-06 12:21
 */
public class DistributeLockTest {
    public static void main(String[] args) {
        final DistributeLock lock1 = new DistributeLock();
        Runnable task01 = () -> {
            lock1.lock();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock1.unlock();
        };

        final DistributeLock lock2 = new DistributeLock();
        Runnable task02 = () -> {
            lock2.lock();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock2.unlock();
        };

        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 10; i++) {
            threadPool.submit(task01);
            threadPool.submit(task02);
        }
        threadPool.shutdown();
    }
}
