package com.timmytime.predictoranalysisplayers.service;

import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;

import java.util.Optional;
import java.util.UUID;

public interface TensorflowTrainingService {

    void train(UUID receipt);

    void receiveReceipt(UUID receipt);

}
