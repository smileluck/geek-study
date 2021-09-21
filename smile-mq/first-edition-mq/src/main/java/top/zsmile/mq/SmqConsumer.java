package top.zsmile.mq;

import java.util.ArrayList;
import java.util.List;

public class SmqConsumer {
    private SmqBroker smqBroker;

    private List<SmqChannel> channels;

    public SmqConsumer(SmqBroker smqBroker) {
        this.smqBroker = smqBroker;
        this.channels = new ArrayList<>();
    }

    public void substribe(String topic) {
        SmqChannel smqChannel = smqBroker.connectTopic(topic);
        channels.add(smqChannel);
    }

    public List<SmqChannel> getChannels() {
        return channels;
    }

    public SmqMessage getMessage(SmqChannel smqChannel) {
        return smqChannel.poll(10);
    }

}
