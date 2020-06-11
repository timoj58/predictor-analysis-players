package com.timmytime.predictoranalysisplayers.callable;

import com.timmytime.predictoranalysisplayers.service.CompetitionService;
import com.timmytime.predictoranalysisplayers.util.LambdaUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public class LoadMatchResponseCache implements Callable {

    private final CompetitionService competitionService;
    private final LambdaUtils lambdaUtils = new LambdaUtils();


    public LoadMatchResponseCache(
            CompetitionService competitionService
    ){
        this.competitionService = competitionService;
    }

    @Override
    public Object call() throws Exception {
        CompletableFuture.runAsync( () -> competitionService.loadMatches())
        .thenRun(() -> lambdaUtils.destroy());
        return null;
    }
}
