package com.timmytime.predictoranalysisplayers.service.impl;


import com.timmytime.predictoranalysisplayers.facade.MatchFacade;
import com.timmytime.predictoranalysisplayers.model.redis.PlayerForm;
import com.timmytime.predictoranalysisplayers.repo.redis.PlayerFormRepo;
import com.timmytime.predictoranalysisplayers.response.data.Match;
import com.timmytime.predictoranalysisplayers.response.data.Player;
import com.timmytime.predictoranalysisplayers.service.PlayerFormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service("playerFormService")
public class PlayerFormServiceImpl implements PlayerFormService {

    private static final Logger log = LoggerFactory.getLogger(PlayerFormServiceImpl.class);

    private final MatchFacade matchFacade;
    private final PlayerFormRepo playerFormRepo;

    @Autowired
    public PlayerFormServiceImpl(
            MatchFacade matchFacade,
            PlayerFormRepo playerFormRepo
    ) {
        this.matchFacade = matchFacade;
        this.playerFormRepo = playerFormRepo;
    }

    @Override
    public void load(Player player) {
        log.info("loading {}", player.getLabel());

        CompletableFuture.runAsync(() -> {
            List<Match> matches = matchFacade.findByTeamsContains(player.getLatestTeam());

            PlayerForm playerForm = new PlayerForm();
            playerForm.setPlayer(player);
            //now sort out the appearances...

            playerFormRepo.save(playerForm);


        });

    }

    @Override
    public void clear() {
        playerFormRepo.deleteAll();
    }

}
