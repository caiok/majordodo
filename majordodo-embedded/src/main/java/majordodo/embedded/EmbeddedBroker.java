/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package majordodo.embedded;

import majordodo.network.netty.NettyChannelAcceptor;
import majordodo.replication.ReplicatedCommitLog;
import majordodo.task.Broker;
import majordodo.task.BrokerConfiguration;
import majordodo.task.FileCommitLog;
import majordodo.task.GroupMapperFunction;
import majordodo.task.MemoryCommitLog;
import majordodo.task.StatusChangesLog;
import majordodo.task.TasksHeap;
import java.nio.file.Paths;

/**
 * Utility to embed a Majordodo Broker
 *
 * @author enrico.olivelli
 */
public class EmbeddedBroker {

    private Broker broker;
    private BrokerConfiguration brokerConfiguration;
    private GroupMapperFunction groupMapperFunction;
    private StatusChangesLog statusChangesLog;
    private NettyChannelAcceptor server;
    private final EmbeddedBrokerConfiguration configuration;

    public EmbeddedBroker(EmbeddedBrokerConfiguration configuration) {
        this.configuration = configuration;
    }

    public EmbeddedBrokerConfiguration getConfiguration() {
        return configuration;
    }

    public GroupMapperFunction getGroupMapperFunction() {
        return groupMapperFunction;
    }

    public void setGroupMapperFunction(GroupMapperFunction groupMapperFunction) {
        this.groupMapperFunction = groupMapperFunction;
    }

    public Broker getBroker() {
        return broker;
    }

    public BrokerConfiguration getBrokerConfiguration() {
        return brokerConfiguration;
    }

    public void start() throws Exception {
        String host = configuration.getStringProperty(EmbeddedBrokerConfiguration.KEY_HOST, "localhost");
        int port = configuration.getIntProperty(EmbeddedBrokerConfiguration.KEY_PORT, 7862);
        String mode = configuration.getStringProperty(EmbeddedBrokerConfiguration.KEY_MODE, EmbeddedBrokerConfiguration.MODE_SIGLESERVER);
        String logDirectory = configuration.getStringProperty(EmbeddedBrokerConfiguration.KEY_LOGSDIRECTORY, "txlog");
        String snapshotsDirectory = configuration.getStringProperty(EmbeddedBrokerConfiguration.KEY_SNAPSHOTSDIRECTORY, "snapshots");
        String zkAdress = configuration.getStringProperty(EmbeddedBrokerConfiguration.KEY_ZKADDRESS, "localhost:1281");
        String zkPath = configuration.getStringProperty(EmbeddedBrokerConfiguration.KEY_ZKPATH, "/majordodo");
        int zkSessionTimeout = configuration.getIntProperty(EmbeddedBrokerConfiguration.KEY_ZKSESSIONTIMEOUT, 40000);

        switch (mode) {
            case EmbeddedBrokerConfiguration.MODE_JVMONLY:
                statusChangesLog = new MemoryCommitLog();                
                break;
            case EmbeddedBrokerConfiguration.MODE_SIGLESERVER:
                statusChangesLog = new FileCommitLog(Paths.get(logDirectory), Paths.get(snapshotsDirectory));
                break;
            case EmbeddedBrokerConfiguration.MODE_CLUSTERED:
                statusChangesLog = new ReplicatedCommitLog(zkAdress, zkSessionTimeout, zkPath, Paths.get(snapshotsDirectory), Broker.formatHostdata(host, port));
                break;
        }
        broker = new Broker(brokerConfiguration, statusChangesLog, new TasksHeap(brokerConfiguration.getTasksHeapSize(), groupMapperFunction));
        switch (mode) {
            case EmbeddedBrokerConfiguration.MODE_JVMONLY:
                broker.setBrokerId("embedded");
                break;
            case EmbeddedBrokerConfiguration.MODE_SIGLESERVER:
            case EmbeddedBrokerConfiguration.MODE_CLUSTERED:
                server = new NettyChannelAcceptor(broker.getAcceptor(), host, port);
                server = new NettyChannelAcceptor(broker.getAcceptor(), host, port);
                break;
        }
        broker.start();
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.close();
        }
        if (broker != null) {
            broker.stop();
        }
    }

}