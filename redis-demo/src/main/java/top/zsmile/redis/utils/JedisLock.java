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
