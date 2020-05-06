package com.timmytime.predictoranalysisplayers.repo.redis;

import com.timmytime.predictoranalysisplayers.model.redis.PlayerForm;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface PlayerFormRepo extends CrudRepository<PlayerForm, UUID> {

}
