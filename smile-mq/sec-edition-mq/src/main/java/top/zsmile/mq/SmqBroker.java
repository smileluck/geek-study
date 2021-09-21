package top.zsmile.mq;

import java.util.HashMap;
import java.util.Map;

public class SmqBroker {
    private final int QUEUE_CAPACITY = 512;
    public final Map<String, SmqChannel> channelMap = new HashMap<String, SmqChannel>(64);

    public SmqChannel createTopic(String topic) {
        SmqChannel channel = new SmqChannel(topic);
        this.channelMap.putIfAbsent(topic, channel);
        return channel;
    }

    public SmqChannel connectTopic(String topic) {
        SmqChannel smqChannel = this.channelMap.get(topic);
        if (smqChannel == null) {
            return createTopic(topic);
        } else {
            return smqChannel;
        }
    }

    public SmqConsumer createConsumer() {
        return new SmqConsumer(this);
    }

    public SmqProducer createProducer() {
        return new SmqProducer(this);
    }

}
