package com.timmytime.predictoranalysisplayers.factory;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class RedissonConnect {

    private final RedissonClient redissonClient;

    @Autowired
    public RedissonConnect(
        @Value("${spring.redis.host}") String host
    ){
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://"+host+":6379");

        redissonClient = Redisson.create(config);
    }

    public RedissonClient connect(){
        return redissonClient;
    }
}
