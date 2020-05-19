package com.timmytime.predictoranalysisplayers.repo.redis;

import com.timmytime.predictoranalysisplayers.model.redis.FantasyResponse;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface FantasyResponseRepo extends CrudRepository<FantasyResponse, UUID> {
}
