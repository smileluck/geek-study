package top.zsmile.mq;

public class SmqProducer {
    private SmqBroker smqBroker;

    public SmqProducer(SmqBroker smqBroker) {
        this.smqBroker = smqBroker;
    }

    public boolean send(String topic, SmqMessage smqMessage) {
        SmqChannel smqChannel = this.smqBroker.connectTopic(topic);
        return smqChannel.send(smqMessage);
    }
}
