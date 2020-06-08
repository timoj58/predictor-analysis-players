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
public class PlayersResponseRepo {

    private final RedissonClient redissonClient;

    @Autowired
    public PlayersResponseRepo(
            @Value("${spring.redis.host}") String host
    ) {
        redissonClient = RedissonConnect.connect(host);
    }

    public void save(UUID team, PlayersResponse playersResponse) throws JsonProcessingException {

        redissonClient.getSet(team.toString(), new org.redisson.client.codec.StringCodec())
                .add(new ObjectMapper().writeValueAsString(playersResponse));
    }

    public void deleteAll(UUID team){
        redissonClient.getSet(team.toString(), new org.redisson.client.codec.StringCodec()).delete();
    }

}
