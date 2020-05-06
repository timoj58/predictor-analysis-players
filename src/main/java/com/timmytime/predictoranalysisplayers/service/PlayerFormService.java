package com.timmytime.predictoranalysisplayers.service;

import com.timmytime.predictoranalysisplayers.model.redis.PlayerForm;
import com.timmytime.predictoranalysisplayers.response.data.Player;

import java.util.UUID;


public interface PlayerFormService {

    void load(Player player);
    PlayerForm get(UUID id);
    void clear();
}
