package com.timmytime.predictoranalysisplayers.service.impl;

import com.timmytime.predictoranalysisplayers.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictoranalysisplayers.facade.TeamFacade;
import com.timmytime.predictoranalysisplayers.facade.TensorflowFacade;
import com.timmytime.predictoranalysisplayers.repo.redis.PlayerFormRepo;
import com.timmytime.predictoranalysisplayers.service.TensorflowTrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.UUID;

@Service("tensorflowTrainingService")
public class TensorflowTrainingServiceImpl implements TensorflowTrainingService {

    private static final Logger log = LoggerFactory.getLogger(TensorflowTrainingServiceImpl.class);

    private final TeamFacade teamFacade;
    private final PlayerFormRepo playerFormRepo;
    private final TensorflowFacade tensorflowFacade;

    @Autowired
    public TensorflowTrainingServiceImpl(
          TeamFacade teamFacade,
          PlayerFormRepo playerFormRepo,
          TensorflowFacade tensorflowFacade
    ){
        this.teamFacade = teamFacade;
        this.playerFormRepo = playerFormRepo;
        this.tensorflowFacade = tensorflowFacade;
    }

    @Override
    public void train(UUID receipt) {
        Arrays.asList(ApplicableFantasyLeagues.values())
                .stream()
                .forEach(competition ->
                        teamFacade.getTeamsByCompetition(competition.name().toLowerCase())
                        .stream()
                        .forEach(team ->
                                playerFormRepo.findByTeam(team.getId())
                                .stream()
                                .forEach(player -> {
                                    //actually at this point need to create all the receipts...and use receipts to execute.

                                    log.info("training {} - {}", player.getLabel(), team.getLabel());
                                    tensorflowFacade.train(player.getId(), null);
                                })
                        )
                );

    }
}
