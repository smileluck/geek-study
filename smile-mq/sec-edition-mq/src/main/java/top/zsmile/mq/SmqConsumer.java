package top.zsmile.mq;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class SmqConsumer {
    private String id;
    private int readIndex;

    private SmqBroker smqBroker;

    private List<SmqChannelVO> channels;

    public String getId() {
        return this.id;
    }

    public SmqConsumer(SmqBroker smqBroker) {
        this.id = UUID.randomUUID().toString();
        this.smqBroker = smqBroker;
        this.channels = new ArrayList<>();
        this.readIndex = 0;
    }

    public void substribe(String topic) {
        channels.add(new SmqChannelVO(smqBroker.connectTopic(topic)));
    }

    public List<SmqChannelVO> getChannels() {
        return channels;
    }

    public SmqMessage getMessage(SmqChannelVO channelVO) {
        SmqMessage message = channelVO.poll();
        return message;
    }

}
