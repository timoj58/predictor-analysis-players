package com.timmytime.predictoranalysisplayers.callable;

import com.timmytime.predictoranalysisplayers.service.TensorflowPredictionService;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public class InitPredict implements Callable {

    private final TensorflowPredictionService predictionService;
    private final UUID receiptId;

    public InitPredict(
            TensorflowPredictionService predictionService,
            UUID receiptId
    ){
        this.predictionService = predictionService;
        this.receiptId = receiptId;
    }

    @Override
    public Object call() throws Exception {
        CompletableFuture.runAsync( () ->  predictionService.predict(receiptId));
        return null;
    }
}
