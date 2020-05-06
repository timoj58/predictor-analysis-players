package com.timmytime.predictoranalysisplayers.service.impl;

import com.timmytime.predictoranalysisplayers.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictoranalysisplayers.facade.PlayerFacade;
import com.timmytime.predictoranalysisplayers.service.CompetitionService;
import com.timmytime.predictoranalysisplayers.service.PlayerFormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Service("competitionService")
public class CompetitionServiceImpl implements CompetitionService {

    private static final Logger log = LoggerFactory.getLogger(CompetitionServiceImpl.class);
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
                .stream()
                .forEach(competition -> load(competition.name().toLowerCase())
                );
    }

    @Override
    public void load(String competition) {

        log.info("loading {}", competition);

        CompletableFuture.runAsync(() ->
        playerFacade.getPlayersByCompetition(competition)
                .stream()
                .filter(f -> f.getLastAppearance().isAfter(LocalDate.now().minusYears(2L))) //limit player form to last two years
                .forEach(player -> playerFormService.load(player))
        ).thenRun(() -> log.info("loaded {}", competition));

    }
}
