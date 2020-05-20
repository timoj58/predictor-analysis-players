package com.timmytime.predictoranalysisplayers.service;

import org.json.JSONObject;

import java.util.UUID;

public interface TensorflowPredictionService {

    void predict(UUID receipt);

    //for testing at present...
    void predict(UUID player,String home, UUID opponent);

    void receiveReceipt(JSONObject results, UUID receipt);

}
