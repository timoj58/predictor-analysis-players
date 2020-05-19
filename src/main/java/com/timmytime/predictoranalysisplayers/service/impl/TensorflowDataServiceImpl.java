package com.timmytime.predictoranalysisplayers.service.impl;

import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.model.redis.Event;
import com.timmytime.predictoranalysisplayers.model.redis.PlayerAppearance;
import com.timmytime.predictoranalysisplayers.model.redis.PlayerForm;
import com.timmytime.predictoranalysisplayers.repo.redis.PlayerFormRepo;
import com.timmytime.predictoranalysisplayers.response.PlayerEventOutcomeCsv;
import com.timmytime.predictoranalysisplayers.service.TensorflowDataService;
import com.timmytime.predictoranalysisplayers.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("tensorflowService")
public class TensorflowDataServiceImpl implements TensorflowDataService {

    private static final Logger log = LoggerFactory.getLogger(TensorflowDataServiceImpl.class);

    private final DateUtils dateUtils = new DateUtils();

    private final PlayerFormRepo playerFormRepo;


    @Autowired
    public TensorflowDataServiceImpl(
            PlayerFormRepo playerFormRepo
    ) {
        this.playerFormRepo = playerFormRepo;
    }

    @Override
    public List<PlayerEventOutcomeCsv> getPlayerCsv(String fromDate, String toDate) {

        log.info("getting player data");

        List<PlayerEventOutcomeCsv> playerEventOutcomeCsvs = new ArrayList<>();


        //we always have, as tensorflow asks by player id..
        playerFormRepo.findAll().forEach(playerForm ->

        playerForm.getPlayerAppearances()
                .stream()
                .filter(f -> f.getDate().equals(dateUtils.getDate.apply(fromDate)) || f.getDate().after(dateUtils.getDate.apply(fromDate)))
                .filter(f -> f.getDate().before(dateUtils.getDate.apply(toDate)))
                .sorted(Comparator.comparing(PlayerAppearance::getDate))
                .forEach(playerAppearance -> {

                    PlayerEventOutcomeCsv playerEventOutcomeCsv = new PlayerEventOutcomeCsv();

                    playerEventOutcomeCsv.setPlayer(playerForm.getId());
                    playerEventOutcomeCsv.setOpponent(playerAppearance.getOpponent());
                    playerEventOutcomeCsv.setHome(playerAppearance.getHome() ? "home" : "away");
                    if(playerAppearance.getDuration().intValue() > 90 || playerAppearance.getDuration().intValue() < 0){
                        log.info("player {} has {} minutes", playerForm.getLabel(), playerAppearance.getDuration());
                        playerEventOutcomeCsv.setMinutes(90);
                    }else {
                        playerEventOutcomeCsv.setMinutes(playerAppearance.getDuration());
                    }

                    //add in the other stats too....TBC.
                    playerEventOutcomeCsv.setAssists(
                            playerAppearance.getStatMetrics().stream().filter(f -> f.getEventType().equals(FantasyEventTypes.ASSISTS)).findFirst().orElse(new Event()).getValue());

                    playerEventOutcomeCsv.setGoals(
                            playerAppearance.getStatMetrics().stream().filter(f -> f.getEventType().equals(FantasyEventTypes.GOALS)).findFirst().orElse(new Event()).getValue());

                    playerEventOutcomeCsv.setConceded(
                            playerAppearance.getStatMetrics().stream().filter(f -> f.getEventType().equals(FantasyEventTypes.GOALS_CONCEDED)).findFirst().orElse(new Event()).getValue());

                    playerEventOutcomeCsv.setSaves(
                            playerAppearance.getStatMetrics().stream().filter(f -> f.getEventType().equals(FantasyEventTypes.SAVES)).findFirst().orElse(new Event()).getValue());

                    playerEventOutcomeCsvs.add(playerEventOutcomeCsv);

                    //note these are not in date order.  which is fine.  i guess.  they for the year.


                }
                )
        );


        log.info("completed player data");

        return playerEventOutcomeCsvs;
    }

}
