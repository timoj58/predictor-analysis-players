package com.timmytime.predictoranalysisplayers.service.impl;

import com.timmytime.predictoranalysisplayers.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictoranalysisplayers.facade.TeamFacade;
import com.timmytime.predictoranalysisplayers.facade.TensorflowFacade;
import com.timmytime.predictoranalysisplayers.repo.redis.PlayerFormRepo;
import com.timmytime.predictoranalysisplayers.service.TensorflowPredictionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;

@Service("tensorflowPredictionService")
public class TensorflowPredictionServiceImpl implements TensorflowPredictionService {

    private static final Logger log = LoggerFactory.getLogger(TensorflowPredictionServiceImpl.class);

    private final TeamFacade teamFacade;
    private final PlayerFormRepo playerFormRepo;
    private final TensorflowFacade tensorflowFacade;


    @Autowired
    public TensorflowPredictionServiceImpl(
             TeamFacade teamFacade,
             PlayerFormRepo playerFormRepo,
             TensorflowFacade tensorflowFacade
    ){
        this.teamFacade = teamFacade;
        this.playerFormRepo = playerFormRepo;
        this.tensorflowFacade = tensorflowFacade;
    }

    @Override
    public void predict(UUID receipt) {
        Arrays.asList(ApplicableFantasyLeagues.values())
                .stream()
                .forEach(competition ->
                        teamFacade.getTeamsByCompetition(competition.name().toLowerCase())
                                .stream()
                                .forEach(team ->
                                        playerFormRepo.findByTeam(team.getId())
                                                .stream()
                                                .forEach(player -> {
                                                    log.info("predicting {}", player.getLabel());
                                                   // tensorflowFacade.predict(player.getId(), null);

                                                    //build receipts the future will do prediction, and store to database
                                                })
                                )
                );

    }
}
