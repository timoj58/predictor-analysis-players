package com.timmytime.predictoranalysisplayers.repo.redisson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictoranalysisplayers.factory.RedissonConnect;
import com.timmytime.predictoranalysisplayers.model.redisson.CompetitionTeamsResponse;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class CompetitionTeamsResponseRepo {

    private final RedissonClient redissonClient;

    @Autowired
    public CompetitionTeamsResponseRepo(
            @Value("${spring.redis.host}") String host
    ) {
        redissonClient = RedissonConnect.connect(host);
    }

    public void save(CompetitionTeamsResponse competitionTeamsResponse) throws JsonProcessingException {

        redissonClient.getSet("CompetitionTeamsResponse", new org.redisson.client.codec.StringCodec())
                .add(new ObjectMapper().writeValueAsString(competitionTeamsResponse));
    }

    public void deleteAll(){
        redissonClient.getSet("CompetitionTeamsResponse", new org.redisson.client.codec.StringCodec()).delete();
    }

}
