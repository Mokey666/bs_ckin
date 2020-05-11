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
    RedisConfig redisConfig; //redis一些基础设置

    @Bean
    public JedisPool JedisPoolFactory() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(500);//最大空闲数
        poolConfig.setMaxTotal(1000);//最大连接数
        poolConfig.setMaxWaitMillis(1000);//最大延迟时间
        JedisPool jp = new JedisPool(poolConfig, "127.0.0.1", 6379,
                1000*1000);//添加resid配置、地址、端口、超时时间
        return jp;
    }
}
