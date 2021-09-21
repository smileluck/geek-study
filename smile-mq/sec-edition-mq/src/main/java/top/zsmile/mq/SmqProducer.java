package top.zsmile.mq;

import java.util.UUID;

public class SmqProducer {
    private String id;
    private SmqBroker smqBroker;

    public SmqProducer(SmqBroker smqBroker) {
        this.id = UUID.randomUUID().toString();
        this.smqBroker = smqBroker;
    }

    public boolean send(String topic, SmqMessage smqMessage) {
        SmqChannel smqChannel = this.smqBroker.connectTopic(topic);
        return smqChannel.send(smqMessage);
    }
}
