package com.timmytime.predictoranalysisplayers.service.impl;

import com.timmytime.predictoranalysisplayers.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictoranalysisplayers.facade.PlayerFacade;
import com.timmytime.predictoranalysisplayers.service.CompetitionService;
import com.timmytime.predictoranalysisplayers.service.PlayerFormService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

public class CompetitionServiceImpl implements CompetitionService {

    private final PlayerFacade playerFacade;
    private final PlayerFormService playerFormService;

    @Autowired
    public CompetitionServiceImpl(
            PlayerFacade playerFacade,
            PlayerFormService playerFormService
    ) {
        this.playerFacade = playerFacade;
        this.playerFormService = playerFormService;
    }

    @Override
    public void load() {

        Arrays.asList(ApplicableFantasyLeagues.values())
                .parallelStream()
                .forEach(competition -> load(competition.name().toLowerCase())
                );
    }

    @Override
    public void load(String competition) {

        playerFacade.getPlayersByCompetition(competition)
                .parallelStream()
                .forEach(player -> playerFormService.load(player));

    }
}
