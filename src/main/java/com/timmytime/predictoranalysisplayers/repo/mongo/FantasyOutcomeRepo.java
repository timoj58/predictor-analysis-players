package com.timmytime.predictoranalysisplayers.repo.mongo;

import com.timmytime.predictoranalysisplayers.model.mongo.FantasyOutcome;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface FantasyOutcomeRepo extends MongoRepository<FantasyOutcome, UUID> {

    List<FantasyOutcome> findByPlayerIdAndSuccessNull(UUID id);
    List<FantasyOutcome> findBySuccessNull();

}
