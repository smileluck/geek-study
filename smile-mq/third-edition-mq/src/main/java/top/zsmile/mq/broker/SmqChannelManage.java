package top.zsmile.mq.broker;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class SmqChannelManage {
    private Map<String, SmqChannel> channelMap = new HashMap<>(64);

//    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private SmqChannel createTopic(String topic) {
        try {
//            readWriteLock.writeLock().lock();
            SmqChannel channel = new SmqChannel(topic);
            this.channelMap.putIfAbsent(topic, channel);
            return channel;
        } finally {
//            readWriteLock.writeLock().unlock();
        }
    }

    public SmqChannel connectTopic(String topic) {
//        readWriteLock.readLock().lock();
        SmqChannel smqChannel = this.channelMap.get(topic);
        if (smqChannel == null) {
//            readWriteLock.readLock().unlock();
            return createTopic(topic);
        } else {
//            readWriteLock.readLock().unlock();
            return smqChannel;
        }
    }
}
