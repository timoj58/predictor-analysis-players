package com.timmytime.predictoranalysisplayers.repo.redis;

import com.timmytime.predictoranalysisplayers.model.redis.PlayerForm;
import com.timmytime.predictoranalysisplayers.response.data.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PlayerFormRepo extends CrudRepository<PlayerForm, Player> {

}
