package top.zsmile.mq;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SmqChannel {
    private String topic;
    private int capacity;
    private LinkedBlockingQueue<SmqMessage> queue;

    public SmqChannel(String topic, int capacity) {
        this.topic = topic;
        this.capacity = capacity;
        this.queue = new LinkedBlockingQueue(capacity);
    }

    public String getTopic() {
        return this.topic;
    }

    public boolean send(SmqMessage smqMessage) {
        return this.queue.offer(smqMessage);
    }

    public SmqMessage poll(int i) {
        SmqMessage result = this.queue.poll();
        return result;
    }

    public SmqMessage pool(long time) {
        return this.poll(time, TimeUnit.MILLISECONDS);
    }

    public SmqMessage poll(long time, TimeUnit timeUnit) {
        try {
            SmqMessage result = this.queue.poll(time, timeUnit);
            return result;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

}
