package com.timmytime.predictoranalysisplayers.repo.redisson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictoranalysisplayers.factory.RedissonConnect;
import com.timmytime.predictoranalysisplayers.model.redisson.TopSelectionsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TopSelectionsResponseRepo implements IRedissonRepo<TopSelectionsResponse>{

    private final RedissonConnect redissonConnect;

    @Autowired
    public TopSelectionsResponseRepo(
            RedissonConnect redissonConnect
    ) {
        this.redissonConnect = redissonConnect;
    }

    @Override
    public void save(TopSelectionsResponse topSelectionsResponse) throws JsonProcessingException {

    }

    public void save(String competition, TopSelectionsResponse topSelectionsResponse) throws JsonProcessingException {

        redissonConnect.connect().getSet("TopSelectionsResponse_"+competition, new org.redisson.client.codec.StringCodec())
                .add(new ObjectMapper().writeValueAsString(topSelectionsResponse));
    }

    @Override
    public void deleteAll() {

    }

    public void deleteAll(String competition){
        redissonConnect.connect().getSet("TopSelectionsResponse_"+competition, new org.redisson.client.codec.StringCodec()).delete();
    }
}
