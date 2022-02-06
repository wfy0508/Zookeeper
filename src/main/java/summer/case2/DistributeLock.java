package summer.case2;

import org.apache.zookeeper.*;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

/**
 * @author summer
 * @title: DistributeLock
 * @projectName Zookeeper
 * @description: 分布式锁测试案例
 * @date 2022-02-05 22:17
 */
public class DistributeLock {
    private ZooKeeper zkClient;
    ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public DistributeLock() {
        try {
            String serversList = "node1:2181,node2:2181,node3:2181";
            int sessionTimeout = 10000;
            zkClient = new ZooKeeper(serversList, sessionTimeout, null);
            // 根节点不存在，则创建
            if (zkClient.exists("/exclusive_lock", false) == null) {
                zkClient.create("/exclusive_lock", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void lock() {
        try {
            // 创建带序号的临时节点
            String currentNode = zkClient.create("/exclusive_lock/" + "seq-", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            // 判断当前节点是否是最小节点，如果是直接获取到锁，如果不是则监听上一个节点，注意根节点不需要监听
            List<String> children = zkClient.getChildren("/exclusive_lock", false);
            if (children.size() > 1) {
                // 多个节点的话，先进行排序
                Collections.sort(children);
                // 获取节点名称seq-0000000
                String thisNode = currentNode.substring("/exclusive_lock/".length());
                // 获取当前节点在节点列表中的位置
                int index = children.indexOf(thisNode);
                if (index == 0) {
                    // 如果是第一个节点，直接获得锁
                    System.out.println(Thread.currentThread().getName() + "获得锁");
                    threadLocal.set(currentNode);
                    return;
                }
                // 否则获取前一个节点
                String preNode = "/exclusive_lock/" + children.get(index - 1);
                Thread thread = Thread.currentThread();
                // 监听前一个节点的变化，如果前一个节点被删除了，会调用回调函数把自己唤醒
                zkClient.getData(preNode, watchedEvent -> LockSupport.unpark(thread), null);
                // 把自己挂起
                LockSupport.park();
            }
            threadLocal.set(currentNode);
            System.out.println(Thread.currentThread().getName() + "获得锁");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unlock() {
        try {
            System.out.println(Thread.currentThread().getName() + "释放了锁");
            zkClient.delete(threadLocal.get(), -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}