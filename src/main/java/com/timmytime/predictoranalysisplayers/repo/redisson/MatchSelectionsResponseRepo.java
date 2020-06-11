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
public class MatchSelectionsResponseRepo implements IRedissonRepo<MatchSelectionsResponse>{

    private final RedissonConnect redissonConnect;

    @Autowired
    public MatchSelectionsResponseRepo(
            RedissonConnect redissonConnect
    ) {
        this.redissonConnect = redissonConnect;
    }

    @Override
    public void save(MatchSelectionsResponse matchSelectionsResponse) throws JsonProcessingException {

    }

    public void save(String competition, MatchSelectionsResponse matchSelectionsResponse) throws JsonProcessingException {

        redissonConnect.connect().getSet("MatchSelectionsResponse_"+competition, new org.redisson.client.codec.StringCodec())
                .add(new ObjectMapper().writeValueAsString(matchSelectionsResponse));
    }

    @Override
    public void deleteAll() {

    }

    public void deleteAll(String competition){
        redissonConnect.connect().getSet("MatchSelectionsResponse_"+competition, new org.redisson.client.codec.StringCodec()).delete();
    }
}
