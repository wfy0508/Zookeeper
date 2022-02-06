package summer.case1;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.omg.PortableServer.THREAD_POLICY_ID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author summer
 * @title: DistributeClient
 * @projectName Zookeeper
 * @description: TODO
 * @date 2022-02-05 17:43
 */
public class DistributeClient {
    private static String connectString = "node1:2181,node2:2181,node3:2181";
    private static int sessionTimeout = 10000;
    private ZooKeeper zk;

    public static void main(String[] args) throws InterruptedException, KeeperException, IOException {
        DistributeClient client = new DistributeClient();
        // 1 获取集群链接
        client.getConnect();

        // 2 监听/server
        client.getServerList();

        // 3 业务功能
        client.business();
    }

    private void business() throws InterruptedException {
        System.out.println("Client is working......");
        Thread.sleep(Long.MAX_VALUE);
    }

    private void getServerList() throws InterruptedException, KeeperException {
        List<String> children = zk.getChildren("/servers", true);
        ArrayList<String> servers = new ArrayList<>();
        for (String child : children) {
            byte[] data = zk.getData("/servers/" + child, false, null);
            servers.add(new String(data));
        }
        System.out.println(servers);
    }

    private void getConnect() throws IOException {
        zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                try {
                    getServerList();
                } catch (InterruptedException | KeeperException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
