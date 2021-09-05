package top.zsmile.redis.pubsub;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class SubDemo {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost", 6379);
        jedis.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                System.out.println("通道：" + channel + ",发来消息:" + message);
            }
        },"channel1");
    }

}
