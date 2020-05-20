package com.timmytime.predictoranalysisplayers.service;

import com.timmytime.predictoranalysisplayers.response.PlayerResponse;

import java.util.UUID;

public interface PlayerResponseService {

    PlayerResponse get(UUID playerId);
}
