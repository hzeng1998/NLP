package com.hzeng;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.Set;

import static com.hzeng.Config.getRedisHost;
import static com.hzeng.Config.getRedisPort;

public class RedisAPI {

    public static JedisPool pool = null;

    public static JedisPool getPool() {

        if (pool == null) {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(50);
            config.setMaxIdle(5);
            config.setMaxWaitMillis(1000 * 100);
            config.setTestOnBorrow(true);
            pool = new JedisPool(config, getRedisHost(), getRedisPort());
        }

        return pool;
    }

    public static void returnResource(JedisPool pool, Jedis redis) {
        if (redis != null) {
            pool.close();
        }
    }

    public static String get(String key) {

        String value = null;

        JedisPool pool = null;
        Jedis jedis = null;

        try {
            pool = getPool();
            jedis = pool.getResource();
            value = jedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }

        return value;
    }

    public static long lpush(String key, String value) {

        JedisPool pool = null;
        Jedis jedis = null;
        long number = 0;

        try {
            pool = getPool();
            jedis = pool.getResource();
            number = jedis.lpush(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }

        return number;
    }

    public static void del(String key) {

        JedisPool pool = null;
        Jedis jedis = null;

        try {
            pool = getPool();
            jedis = pool.getResource();
            jedis.del(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }
    }

    public static List<String> lrange(String key, int start, int end) {

        JedisPool pool = null;
        Jedis jedis = null;
        List<String> value = null;

        try {
            pool = getPool();
            jedis = pool.getResource();
            value = jedis.lrange(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }

        return value;
    }

    public static Set<String> keys(String pattern) {

        JedisPool pool = null;
        Jedis jedis = null;
        Set<String> key_set = null;

        try {
            pool = getPool();
            jedis = pool.getResource();
            key_set = jedis.keys(pattern);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }

        return key_set;
    }
}
