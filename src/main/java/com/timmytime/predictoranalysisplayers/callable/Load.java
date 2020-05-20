package com.timmytime.predictoranalysisplayers.callable;

import com.timmytime.predictoranalysisplayers.service.CompetitionService;

import java.util.UUID;
import java.util.concurrent.Callable;

public class Load implements Callable {

    private final CompetitionService competitionService;
    private final UUID receiptId;

    public Load(
            CompetitionService competitionService,
            UUID receiptId
    ){
        this.competitionService = competitionService;
        this.receiptId = receiptId;
    }

    @Override
    public Object call() throws Exception {
        competitionService.load(receiptId);
        return null;
    }
}
