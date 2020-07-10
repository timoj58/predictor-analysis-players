package com.timmytime.predictoranalysisplayers.service.impl;

import com.timmytime.predictoranalysisplayers.facade.EventFacade;
import com.timmytime.predictoranalysisplayers.facade.PlayerFacade;
import com.timmytime.predictoranalysisplayers.facade.TeamFacade;
import com.timmytime.predictoranalysisplayers.facade.TensorflowFacade;
import com.timmytime.predictoranalysisplayers.receipt.ReceiptManager;
import com.timmytime.predictoranalysisplayers.receipt.ReceiptUtils;
import com.timmytime.predictoranalysisplayers.repo.mongo.FantasyOutcomeRepo;
import com.timmytime.predictoranalysisplayers.repo.redis.PlayerFormRepo;
import com.timmytime.predictoranalysisplayers.repo.redisson.*;
import com.timmytime.predictoranalysisplayers.response.data.Team;
import com.timmytime.predictoranalysisplayers.response.data.UpcomingCompetitionEventsResponse;
import com.timmytime.predictoranalysisplayers.response.data.UpcomingEventResponse;
import com.timmytime.predictoranalysisplayers.service.*;
import com.timmytime.predictoranalysisplayers.util.LambdaUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AutomationServiceImplTest {


    private final PlayerFacade playerFacade = mock(PlayerFacade.class);
    private final TeamFacade teamFacade = mock(TeamFacade.class);
    private final PlayerFormService playerFormService = mock(PlayerFormService.class);
    private final CompetitionTeamsResponseRepo competitionTeamsResponseRepo = mock(CompetitionTeamsResponseRepo.class);
    private final EventFacade eventFacade = mock(EventFacade.class);
    private final IRedissonRepo matchSelectionsResponseRepo = mock(MatchSelectionsResponseRepo.class);
    private final IRedissonRepo topSelectionsResponseRepo = mock(TopSelectionsResponseRepo.class);
    private final ReceiptManager receiptManager = new ReceiptManager( new ReceiptUtils());
    private final LambdaUtils lambdaUtils = mock(LambdaUtils.class);

    private final FantasyOutcomeRepo fantasyOutcomeRepo = mock(FantasyOutcomeRepo.class);
    private final PlayerFormRepo playerFormRepo = mock(PlayerFormRepo.class);
    private final  PlayersResponseRepo playersResponseRepo = mock(PlayersResponseRepo.class);
    private final TensorflowFacade tensorflowFacade = mock(TensorflowFacade.class);

    private final PlayerResponseService playerResponseService = new PlayerResponseServiceImpl(
            fantasyOutcomeRepo, playerFormRepo, playersResponseRepo, teamFacade, receiptManager
    );

    //break this up
    private final CompetitionService competitionService = new CompetitionServiceImpl(
            playerFacade, teamFacade, playerFormService, competitionTeamsResponseRepo, receiptManager, 1L
    );
    private final TensorflowTrainingService trainingService = new TensorflowTrainingServiceImpl(
            teamFacade, playerFormRepo, tensorflowFacade, receiptManager
    );
    private final TensorflowPredictionService predictionService = new TensorflowPredictionServiceImpl(
            teamFacade, eventFacade, playerFormRepo, tensorflowFacade, receiptManager, fantasyOutcomeRepo
    );
    private final ValidationService validationService = new ValidationServiceImpl(
            playerFacade, playerFormRepo, fantasyOutcomeRepo, lambdaUtils, receiptManager
    );

    private final MatchServiceImpl matchService = new MatchServiceImpl(
            teamFacade, eventFacade, playerFormService, playerResponseService, matchSelectionsResponseRepo, topSelectionsResponseRepo
    );

    private final AutomationServiceImpl automationService
            = new AutomationServiceImpl(
                    receiptManager,
            competitionService,
            trainingService,
            predictionService,
            validationService,
            playerResponseService,
            matchService,
            lambdaUtils
    );

    @Test
    void start() {

        Team team = new Team();
        team.setId(UUID.randomUUID());
        team.setCompetition("test");
        team.setLabel("test");
        team.setCountry("test");
        when(teamFacade.getTeamsByCompetition(anyString())).thenReturn(Arrays.asList(team));

        automationService.start();

        waiting();

        verify(playerFormService, atMost(1)).saveActiveByYear();
        verify(lambdaUtils, atMost(1)).startMachineLearning();
        verify(fantasyOutcomeRepo, atMost(1)).findBySuccessNull();
        verify(lambdaUtils, atMost(1)).destroy();
        verify(playersResponseRepo, atLeastOnce()).deleteAll(anyString());
        verify(playerFormRepo, atLeastOnce()).findByTeam(any(UUID.class));
        verify(matchSelectionsResponseRepo, atLeastOnce()).deleteAll(anyString());
        verify(topSelectionsResponseRepo, atLeastOnce()).deleteAll(anyString());


    }

    public synchronized void waiting(){
        try {//90000L
            this.wait(400L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}