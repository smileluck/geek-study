package top.zsmile.redis.lock;

import redis.clients.jedis.Jedis;
import top.zsmile.redis.utils.JedisLock;

/**
 * 作业8.2 分布式计数器减库存
 */
public class JedisDemo2 {
    public static void main(String[] args) throws InterruptedException {
        Jedis jedis = new Jedis("localhost", 6379);
        JedisLock jedisLock = new JedisLock(jedis);
        String lockName = "testLock";

        jedis.set(lockName, "20");

        new Thread(() -> {
            while (true) {
                Long decr = jedisLock.decr(lockName, "0");
                if (decr==0) {
                    System.out.println("无库存了1");
                    break;
                } else {
                    System.out.println("减库存1，当前库存:" + decr);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Thread.sleep(1000);
        new Thread(() -> {
            while (true) {
                 Long decr = jedisLock.decr(lockName, "0");
                if (decr==0) {
                    System.out.println("无库存了2");
                    break;
                } else {
                    System.out.println("减库存2，当前库存:" + decr);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
