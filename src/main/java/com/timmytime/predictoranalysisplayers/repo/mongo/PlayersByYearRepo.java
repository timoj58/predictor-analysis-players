package com.timmytime.predictoranalysisplayers.repo.mongo;

import com.timmytime.predictoranalysisplayers.model.mongo.PlayersByYear;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface PlayersByYearRepo extends MongoRepository<PlayersByYear, Integer> {
}
