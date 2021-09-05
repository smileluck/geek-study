package top.zsmile.redis.lock;

import redis.clients.jedis.Jedis;
import top.zsmile.redis.utils.JedisLock;

/**
 * 作业8.1 简单分布式锁
 */
public class JedisDemo {
    public static void main(String[] args) throws InterruptedException {
        Jedis jedis = new Jedis("localhost", 6379);
        JedisLock jedisLock = new JedisLock(jedis);

        String lockName = "testLock";

        new Thread(() -> {
            if (jedisLock.lock(lockName, 5)) {
                System.out.println("获取锁成功1");
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (jedisLock.unlock(lockName)) {
                    System.out.println("释放锁成功1");
                } else {
                    System.out.println("释放锁失败1");
                }
            } else {
                System.out.println("获取锁失败1");
            }
        }).start();

        new Thread(() -> {
            while (true) {
                if (jedisLock.lock(lockName, 10)) {
                    System.out.println("获取锁成功2");
                    if (jedisLock.unlock(lockName)) {
                        System.out.println("释放锁成功2");
                    } else {
                        System.out.println("释放锁失败2");
                    }
                    break;
                } else {
                    System.out.println("获取锁失败2");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
//        thread.start();

    }
}
