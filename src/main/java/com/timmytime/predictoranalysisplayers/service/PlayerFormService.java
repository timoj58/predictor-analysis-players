package com.timmytime.predictoranalysisplayers.service;

import com.timmytime.predictoranalysisplayers.model.redis.PlayerForm;
import com.timmytime.predictoranalysisplayers.response.PlayersByTeam;
import com.timmytime.predictoranalysisplayers.response.data.Player;

import java.util.List;
import java.util.UUID;


public interface PlayerFormService {

    void load(Player player, Boolean firstTime);
    PlayerForm get(UUID id);
    PlayersByTeam getPlayers(UUID team);
    List<Player> getPlayers();
    void clear();
    Boolean firstTime();
}
