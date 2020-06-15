package com.timmytime.predictoranalysisplayers.callable;

import com.timmytime.predictoranalysisplayers.service.PlayerResponseService;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public class LoadPlayerResponseCache implements Callable {

    private final PlayerResponseService playerResponseService;
    private final UUID receipt;

    public LoadPlayerResponseCache(
            PlayerResponseService playerResponseService,
            UUID receipt
    ){
        this.playerResponseService = playerResponseService;
        this.receipt = receipt;
    }

    @Override
    public Object call() throws Exception {
        int i;
        CompletableFuture.runAsync( () -> playerResponseService.load(receipt));
         return null;
    }
}
