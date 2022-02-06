package summer.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;
import org.omg.PortableInterceptor.LOCATION_FORWARD;

import java.io.IOException;
import java.util.List;

/**
 * @author summer
 * @title: zkClient
 * @projectName Zookeeper
 * @description: TODO
 * @date 2022-02-05 16:31
 */
public class zkClient {
    private static String connectString = "node1:2181,node2:2181,node3:2181";
    private static int sessionTimeout = 100000;
    private ZooKeeper zkClient = null;

    @Before
    public void init() throws IOException {

        zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("--------------------");
                List<String> children = null;
                try {
                    children = zkClient.getChildren("/", true);
                    for (String child : children) {
                        System.out.println(child);
                    }
                    System.out.println("----------------------");
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 创建永久节点
     */
    @Test
    public void create() throws InterruptedException, KeeperException {
        String nodeCreated = zkClient.create("/sanguo/weiguo", "caocao".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    /**
     * 获取节点信息
     */
    @Test
    public void getChildren() throws InterruptedException, KeeperException {
        // 这里的watch注册一次使用一次
        List<String> children = zkClient.getChildren("/", true);
        for (String child : children) {
            System.out.println(child);
        }
        Thread.sleep(Long.MAX_VALUE);
    }

    /**
     * 判断某个节点是否存在
     */
    @Test
    public void exists() throws InterruptedException, KeeperException {
        final Stat stat = zkClient.exists("/sanguo", true);
        System.out.println(stat == null ? "not exist" : "exist");
    }
}
