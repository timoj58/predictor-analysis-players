package com.timmytime.predictoranalysisplayers.service;

import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;

import java.util.UUID;

public interface TensorflowTrainingService {

    void train(UUID receipt);

    void trainPlayer(UUID player, FantasyEventTypes fantasyEventTypes);

    void receiveReceipt(UUID receipt);

}
