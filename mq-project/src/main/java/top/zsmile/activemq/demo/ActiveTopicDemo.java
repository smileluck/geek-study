package top.zsmile.activemq.demo;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

import javax.jms.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ActiveTopicDemo {
    public static void main(String[] args) {
        Destination activeMQQueue = new ActiveMQTopic("testTopic");

        testQueue(activeMQQueue);
    }

    public static void testQueue(Destination destination) {
        try {
            ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory("tcp://127.0.0.1:61616");

            Connection connection = activeMQConnectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            MessageConsumer consumer = session.createConsumer(destination);
            AtomicInteger atomicInteger = new AtomicInteger(0);
            MessageListener messageListener = new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    System.out.println("consumer1：" + atomicInteger.incrementAndGet() + "==> receive " + message);
                }
            };
            consumer.setMessageListener(messageListener);


            MessageConsumer consumer2 = session.createConsumer(destination);
            AtomicInteger atomicInteger2 = new AtomicInteger(0);
            MessageListener messageListener2 = new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    System.out.println("consumer2：" + atomicInteger2.incrementAndGet() + "==> receive " + message);
                }
            };
            consumer2.setMessageListener(messageListener2);

            MessageProducer producer = session.createProducer(destination);
            int index = 0;
            while (index++ < 100) {
                TextMessage message = session.createTextMessage(index + " message.");
                producer.send(message);
            }

            Thread.sleep(20000);

            session.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
