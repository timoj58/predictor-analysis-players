package com.timmytime.predictoranalysisplayers.service;

import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.response.MatchPrediction;
import com.timmytime.predictoranalysisplayers.response.TopPerformerResponse;

import java.util.List;
import java.util.UUID;

public interface CompetitionService {
    void load(UUID receiptId);
  }
