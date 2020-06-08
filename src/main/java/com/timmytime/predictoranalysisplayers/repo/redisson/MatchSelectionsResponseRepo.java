package com.timmytime.predictoranalysisplayers.repo.redisson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictoranalysisplayers.factory.RedissonConnect;
import com.timmytime.predictoranalysisplayers.model.redisson.MatchSelectionsResponse;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MatchSelectionsResponseRepo {

    private final RedissonClient redissonClient;

    @Autowired
    public MatchSelectionsResponseRepo(
            @Value("${spring.redis.host}") String host
    ) {
        redissonClient = RedissonConnect.connect(host);
    }

    public void save(String competition, MatchSelectionsResponse matchSelectionsResponse) throws JsonProcessingException {

        redissonClient.getSet("MatchSelectionsResponse_"+competition, new org.redisson.client.codec.StringCodec())
                .add(new ObjectMapper().writeValueAsString(matchSelectionsResponse));
    }

    public void deleteAll(String competition){
        redissonClient.getSet("MatchSelectionsResponse_"+competition, new org.redisson.client.codec.StringCodec()).delete();
    }
}
