package top.zsmile.mq;

import lombok.Data;
import lombok.Getter;

public class SmqChannelVO {
    private int readIndex;
    private SmqChannel channel;

    public SmqChannelVO(SmqChannel smqChannel) {
        this.readIndex = 0;
        this.channel = smqChannel;
    }

    public SmqMessage poll() {
        SmqMessage message = this.channel.poll(readIndex);
        if (message != null) {
            this.readIndex++;
        }
        return message;
    }

    public String getTopic() {
        return channel.getTopic();
    }
}
