package com.timmytime.predictoranalysisplayers.callable;

import com.timmytime.predictoranalysisplayers.receipt.ReceiptManager;
import com.timmytime.predictoranalysisplayers.service.CompetitionService;
import com.timmytime.predictoranalysisplayers.service.MatchService;
import com.timmytime.predictoranalysisplayers.util.LambdaUtils;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public class LoadMatchResponseCache implements Callable {

    private final MatchService matchService;
    private final LambdaUtils lambdaUtils;
    private final ReceiptManager receiptManager;
    private final UUID receipt;


    public LoadMatchResponseCache(
            MatchService matchService,
            LambdaUtils lambdaUtils,
            ReceiptManager receiptManager,
            UUID receipt
    ){
        this.matchService = matchService;
        this.lambdaUtils = lambdaUtils;
        this.receiptManager = receiptManager;
        this.receipt = receipt;
    }

    @Override
    public Object call() throws Exception {
        CompletableFuture.runAsync( () -> matchService.loadMatches())
        .thenRun(() -> lambdaUtils.destroy())
        .thenRun(() -> receiptManager.receiptReceived.accept(receipt));
        return null;
    }
}
