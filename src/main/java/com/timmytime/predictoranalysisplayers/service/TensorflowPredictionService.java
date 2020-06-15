package com.timmytime.predictoranalysisplayers.service;

import org.json.JSONObject;

import java.util.UUID;

public interface TensorflowPredictionService {

    void predict(UUID receipt);
    void receiveReceipt(JSONObject results, UUID receipt);

}
