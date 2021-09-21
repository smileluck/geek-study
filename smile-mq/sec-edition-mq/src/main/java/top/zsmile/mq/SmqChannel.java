package top.zsmile.mq;


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

    public SmqMessage poll(int readIndex) {
        SmqMessage result = this.queue.read(readIndex);
        return result;
    }

}
