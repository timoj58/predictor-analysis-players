package com.timmytime.predictoranalysisplayers.service;

import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.response.PlayerResponse;
import com.timmytime.predictoranalysisplayers.response.TopPerformerResponse;

import java.util.List;
import java.util.UUID;

public interface PlayerResponseService {

    PlayerResponse get(UUID playerId);
    List<TopPerformerResponse> topPerformers(String competition, FantasyEventTypes fantasyEventTypes);
    List<TopPerformerResponse> topPicks(String competition, FantasyEventTypes fantasyEventTypes);
    void load(UUID receipt);
}
