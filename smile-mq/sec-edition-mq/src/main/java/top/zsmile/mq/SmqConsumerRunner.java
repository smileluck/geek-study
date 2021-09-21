package top.zsmile.mq;

import java.util.List;

public class SmqConsumerRunner implements Runnable {
    private SmqConsumer consumer;

    public SmqConsumerRunner(SmqConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void run() {
        while (true) {
            List<SmqChannelVO> channels = consumer.getChannels();
            for (SmqChannelVO channelVO : channels) {
                SmqMessage message = consumer.getMessage(channelVO);
                if (null != message) {
                    System.out.println(consumer.getId() + "_" + channelVO.getTopic() + " receive:" + message.getBody());
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
