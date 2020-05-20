package com.timmytime.predictoranalysisplayers.callable;

import com.timmytime.predictoranalysisplayers.service.TensorflowTrainingService;

import java.util.UUID;
import java.util.concurrent.Callable;

public class InitTrain implements Callable {

    private final TensorflowTrainingService trainingService;
    private final UUID receiptId;

    public InitTrain(
            TensorflowTrainingService trainingService,
            UUID receiptId
    ){
        this.trainingService = trainingService;
        this.receiptId = receiptId;
    }

    @Override
    public Object call() throws Exception {
        trainingService.train(receiptId);
        return null;
    }
}
