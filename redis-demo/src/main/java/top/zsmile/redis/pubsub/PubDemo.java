package top.zsmile.redis.pubsub;

import redis.clients.jedis.Jedis;

import java.util.Random;

public class PubDemo {
    public static void main(String[] args) throws InterruptedException {
        Jedis jedis = new Jedis("localhost", 6379);
        Random random = new Random();
        int i = 1;
        while (i <= 10) {

            OrderEntity orderEntity = new OrderEntity(random.nextInt(100) + 1, random.nextInt(100) + 10);
            jedis.publish("channel1", orderEntity.toString());
            i++;
            Thread.sleep(100);
        }
    }
}
