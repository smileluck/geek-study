package top.zsmile.activemq.demo;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnection;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.store.memory.MemoryPersistenceAdapter;

import java.net.URI;

public class ActiveBrokerDemo {

    public static void main(String[] args) throws Exception {
        BrokerService brokerService = new BrokerService();
        brokerService.setBrokerName("zsmile");
        brokerService.setUseJmx(true);
        TransportConnector transportConnector = new TransportConnector();
        transportConnector.setUri(new URI("tcp://127.0.0.1:61616"));
        brokerService.addConnector(transportConnector);
        brokerService.start();

        System.out.println("activemq broker start...");
        System.in.read();
    }
}
