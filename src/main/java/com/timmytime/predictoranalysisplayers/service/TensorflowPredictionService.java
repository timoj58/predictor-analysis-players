package com.timmytime.predictoranalysisplayers.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.model.redis.FantasyResponse;
import org.json.JSONObject;

import java.util.UUID;

public interface TensorflowPredictionService {

    void predict(UUID receipt);

    void predict(UUID player, FantasyEventTypes fantasyEventTypes, String home, UUID opponent);

    void predict(UUID player,String home, UUID opponent);

    void receiveReceipt(JSONObject results, UUID receipt);

    FantasyResponse getFantasyResponse(UUID player);

}
