package top.zsmile.mq.broker;


import top.zsmile.mq.SmqMessage;

public class SmqChannel {
    private String topic;
    private SmqQueue queue;

    public SmqChannel(String topic) {
        this.topic = topic;
        this.queue = new SmqQueue();
    }

    public String getTopic() {
        return this.topic;
    }

    public boolean send(SmqMessage smqMessage) {
        return this.queue.write(smqMessage);
    }

    public SmqMessage get(int readIndex) {
        SmqMessage result = this.queue.read(readIndex);
        return result;
    }

    public SmqMessage offset() {
        SmqMessage result = this.queue.readAndOffset();
        return result;
    }

}
