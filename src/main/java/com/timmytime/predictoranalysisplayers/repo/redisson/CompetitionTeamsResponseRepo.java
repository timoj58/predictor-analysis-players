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
public class CompetitionTeamsResponseRepo implements IRedissonRepo<CompetitionTeamsResponse> {

    private final RedissonConnect redissonConnect;

    @Autowired
    public CompetitionTeamsResponseRepo(
           RedissonConnect redissonConnect
    ) {
        this.redissonConnect = redissonConnect;
    }

    public void save(CompetitionTeamsResponse competitionTeamsResponse) throws JsonProcessingException {

        redissonConnect.connect().getSet("CompetitionTeamsResponse", new org.redisson.client.codec.StringCodec())
                .add(new ObjectMapper().writeValueAsString(competitionTeamsResponse));
    }

    @Override
    public void save(String key, CompetitionTeamsResponse competitionTeamsResponse) {

    }

    public void deleteAll(){
        redissonConnect.connect().getSet("CompetitionTeamsResponse", new org.redisson.client.codec.StringCodec()).delete();
    }

    @Override
    public void deleteAll(String key) {

    }

}
