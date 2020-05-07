package com.timmytime.predictoranalysisplayers.service;

import java.util.UUID;

public interface TensorflowTrainingService {

    void train(UUID receipt);

}
