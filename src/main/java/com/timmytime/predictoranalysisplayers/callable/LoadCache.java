package com.timmytime.predictoranalysisplayers.callable;

import com.timmytime.predictoranalysisplayers.service.PlayerResponseService;

import java.util.concurrent.Callable;

public class LoadCache implements Callable {

    private final PlayerResponseService playerResponseService;

    public LoadCache(
            PlayerResponseService playerResponseService
    ){
        this.playerResponseService = playerResponseService;
    }

    @Override
    public Object call() throws Exception {
         playerResponseService.load();
         return null;
    }
}
