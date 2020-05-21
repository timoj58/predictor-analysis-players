package com.timmytime.predictoranalysisplayers.repo.redis;

import com.timmytime.predictoranalysisplayers.model.redis.ActivePlayersByYear;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivePlayersByYearRepo extends CrudRepository<ActivePlayersByYear, Integer> {
}
