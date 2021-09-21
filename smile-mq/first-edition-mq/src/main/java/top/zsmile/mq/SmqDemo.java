package top.zsmile.mq;

import lombok.SneakyThrows;

import java.util.List;

public class SmqDemo {
    @SneakyThrows
    public static void main(String[] args) {
        SmqBroker smqBroker = new SmqBroker();
        SmqConsumer consumer = smqBroker.createConsumer();

        consumer.substribe("test1");
        new Thread(() -> {
            while (true) {
                List<SmqChannel> channels = consumer.getChannels();
                for (SmqChannel smqChannel : channels) {
                    SmqMessage message = smqChannel.poll(10);

                    if (null != message) {
                        System.out.println(smqChannel.getTopic() + "=>" + message.getBody());
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        SmqProducer smqProducer = smqBroker.createProducer();
        for (int i = 1; i <= 10; i++) {
            smqProducer.send("test1", new SmqMessage(null, "int i=>" + i));
        }
        Thread.sleep(500);
        System.out.println("点击任何键，发送一条消息；点击q或e，退出程序。");
        boolean s = false;
        while (true) {
            char c = (char) System.in.read();
            if (c == 'a') {
                if(!s) {
                    consumer.substribe("test2");
                    s = true;
                }
//                smqProducer.send("test2", new SmqMessage(null, "char c=>" + c));
            }

            if (c > 20) {
                System.out.println(c);
                smqProducer.send("test1", new SmqMessage(null, "char c=>" + c));
                if (s) {
                    smqProducer.send("test2", new SmqMessage(null, "char c=>" + c));
                }
            }


            if (c == 'q' || c == 'e') break;
        }
    }
}
