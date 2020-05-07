package com.timmytime.predictoranalysisplayers.service;

import java.util.UUID;

public interface TensorflowPredictionService {

    void predict(UUID receipt);

}
