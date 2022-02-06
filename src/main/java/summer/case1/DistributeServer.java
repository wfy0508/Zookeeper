package summer.case1;

import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * @author summer
 * @title: DistributeServer
 * @projectName Zookeeper
 * @description: TODO
 * @date 2022-02-05 17:43
 */
public class DistributeServer {
    private static String connectString = "node1:2181,node2:2181,node3:2181";
    private int sessionTimeout = 10000;
    private ZooKeeper zk;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        DistributeServer server = new DistributeServer();

        // 1 获取集群链接
        server.getConnect();

        // 2 注册服务器，创建一个节点
        server.register(args[0]);

        // 3 业务功能
        server.business();
    }

    private void business() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    private void register(String hostname) throws InterruptedException, KeeperException {
        String create = zk.create("/servers/" + hostname, hostname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(hostname + " is online.");
    }

    private void getConnect() throws IOException {

        zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

            }
        });
    }

}
