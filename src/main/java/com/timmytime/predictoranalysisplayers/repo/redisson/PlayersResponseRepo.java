package com.timmytime.predictoranalysisplayers.repo.redisson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictoranalysisplayers.factory.RedissonConnect;
import com.timmytime.predictoranalysisplayers.model.redisson.PlayersResponse;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PlayersResponseRepo implements IRedissonRepo<PlayersResponse> {

    private final RedissonConnect redissonConnect;

    @Autowired
    public PlayersResponseRepo(
            RedissonConnect redissonConnect
    ) {
        this.redissonConnect = redissonConnect;
    }

    @Override
    public void save(PlayersResponse playersResponse) throws JsonProcessingException {

    }

    public void save(String team, PlayersResponse playersResponse) throws JsonProcessingException {

        redissonConnect.connect().getSet(team, new org.redisson.client.codec.StringCodec())
                .add(new ObjectMapper().writeValueAsString(playersResponse));
    }

    @Override
    public void deleteAll() {

    }

    public void deleteAll(String team){
        redissonConnect.connect().getSet(team, new org.redisson.client.codec.StringCodec()).delete();
    }

}
