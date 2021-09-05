[toc]

---

# 题目



**1.（选做）**按照课程内容，动手验证 Hibernate 和 Mybatis 缓存。

**2.（选做）**使用 spring 或 guava cache，实现业务数据的查询缓存。

**3.（挑战☆）**编写代码，模拟缓存穿透，击穿，雪崩。

**4.（挑战☆☆）**自己动手设计一个简单的 cache，实现过期策略。

**5.（选做）**命令行下练习操作 Redis 的各种基本数据结构和命令。

**6.（选做）**分别基于 jedis，RedisTemplate，Lettuce，Redission 实现 redis 基本操作的 demo，可以使用 spring-boot 集成上述工具。

**7.（选做）**spring 集成练习:

- 实现 update 方法，配合 @CachePut
- 实现 delete 方法，配合 @CacheEvict
- 将示例中的 spring 集成 Lettuce 改成 jedis 或 redisson

**8.（必做）**基于 Redis 封装分布式数据操作：

- 在 Java 中实现一个简单的分布式锁；
- 在 Java 中实现一个分布式计数器，模拟减库存。

**9.（必做）**基于 Redis 的 PubSub 实现订单异步处理

**10.（挑战☆）**基于其他各类场景，设计并在示例代码中实现简单 demo：

- 实现分数排名或者排行榜；
- 实现全局 ID 生成；
- 基于 Bitmap 实现 id 去重；
- 基于 HLL 实现点击量计数；
- 以 redis 作为数据库，模拟使用 lua 脚本实现前面课程的外汇交易事务。

**11.（挑战☆☆）**升级改造项目：

- 实现 guava cache 的 spring cache 适配；
- 替换 jackson 序列化为 fastjson 或者 fst，kryo；
- 对项目进行分析和性能调优。

**12.（挑战☆☆☆）**以 redis 作为基础实现上个模块的自定义 rpc 的注册中心。





# 作业8

测试代码：[代码地址](https://github.com.cnpmjs.org/smileluck/geek-study/tree/main/redis-demo/src/main/java/top/zsmile/redis/lock)

分布式锁和减库存代码：

```java

package top.zsmile.redis.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

public class JedisLock {
    private Jedis jedis;

    public JedisLock(Jedis jedis) {
        this.jedis = jedis;
    }

    public boolean lock(String key, String value, int seconds) {
        SetParams setParams = new SetParams();
        setParams.nx().px(seconds * 1000);
        String set = jedis.set(key, value, setParams);
        if ("ok".equalsIgnoreCase(set)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean lock(String key, int seconds) {
        return lock(key, key, seconds);
    }

    public boolean unlock(String key) {
        String luaStr = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        Object num = jedis.eval(luaStr, 1, key, key);
        if (num.equals(0)) {
            return false;
        } else {
            return true;
        }
    }


    public Long decr(String key, String value) {
        String luaStr = "if redis.call('get',KEYS[1]) > ARGV[1] then return redis.call('decr',KEYS[1]) else return 0 end";
        Object num = jedis.eval(luaStr, 1, key, value);
        return (Long) num;
    }
}


```

## 在 Java 中实现一个简单的分布式锁；

测试代码：[源代码地址]()

```java
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

```






## 在 Java 中实现一个分布式计数器，模拟减库存。

```java
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

```

输出结果：

```

减库存2，当前库存:19
减库存1，当前库存:18
减库存2，当前库存:17
减库存1，当前库存:16
减库存2，当前库存:15
减库存1，当前库存:14
减库存2，当前库存:13
减库存1，当前库存:12
减库存2，当前库存:11
减库存1，当前库存:10
减库存2，当前库存:9
减库存1，当前库存:8
减库存2，当前库存:7
减库存1，当前库存:6
减库存2，当前库存:5
减库存1，当前库存:4
减库存2，当前库存:3
减库存1，当前库存:2
减库存2，当前库存:1
无库存了1
无库存了2

```

# 作业9

测试代码：[代码地址](https://github.com.cnpmjs.org/smileluck/geek-study/tree/main/redis-demo/src/main/java/top/zsmile/redis/pubsub)

发布消息

```java

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

```

接收消息

```java
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

```

执行结果：

```

通道：channel1,发来消息:OrderEntity(id=31, money=49)
通道：channel1,发来消息:OrderEntity(id=68, money=61)
通道：channel1,发来消息:OrderEntity(id=38, money=13)
通道：channel1,发来消息:OrderEntity(id=39, money=57)
通道：channel1,发来消息:OrderEntity(id=77, money=67)
通道：channel1,发来消息:OrderEntity(id=63, money=55)
通道：channel1,发来消息:OrderEntity(id=25, money=60)
通道：channel1,发来消息:OrderEntity(id=28, money=82)
通道：channel1,发来消息:OrderEntity(id=57, money=21)
通道：channel1,发来消息:OrderEntity(id=48, money=22)
```

