package com.redis;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class RedisPool{

    @Autowired
    RedisConfig redisConfig;

    @Bean
    public JedisPool JedisPoolFactory() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(500);
        poolConfig.setMaxTotal(1000);
        poolConfig.setMaxWaitMillis(1000);
        JedisPool jp = new JedisPool(poolConfig, "127.0.0.1", 6379,
                1000*1000);
        return jp;
    }
}
