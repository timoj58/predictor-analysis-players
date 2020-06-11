package com.timmytime.predictoranalysisplayers.repo.redisson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.timmytime.predictoranalysisplayers.model.redisson.RedissonResponse;

public interface IRedissonRepo<T extends RedissonResponse> {

    void save(T t) throws JsonProcessingException;
    void save(String key, T t) throws JsonProcessingException;
    void deleteAll();
    void deleteAll(String key);
}
