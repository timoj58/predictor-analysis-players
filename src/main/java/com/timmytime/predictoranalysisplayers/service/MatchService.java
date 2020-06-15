package com.timmytime.predictoranalysisplayers.service;

import com.timmytime.predictoranalysisplayers.response.MatchPrediction;

import java.util.UUID;

public interface MatchService {
    MatchPrediction get(UUID home, UUID away);
    void loadMatches();
}
