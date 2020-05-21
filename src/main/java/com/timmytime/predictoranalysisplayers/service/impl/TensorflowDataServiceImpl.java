package com.timmytime.predictoranalysisplayers.service.impl;

import com.timmytime.predictoranalysisplayers.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.facade.TeamFacade;
import com.timmytime.predictoranalysisplayers.model.redis.Event;
import com.timmytime.predictoranalysisplayers.model.redis.PlayerAppearance;
import com.timmytime.predictoranalysisplayers.model.redis.PlayerForm;
import com.timmytime.predictoranalysisplayers.repo.redis.ActivePlayersByYearRepo;
import com.timmytime.predictoranalysisplayers.repo.redis.PlayerFormRepo;
import com.timmytime.predictoranalysisplayers.response.PlayerEventOutcomeCsv;
import com.timmytime.predictoranalysisplayers.response.data.Team;
import com.timmytime.predictoranalysisplayers.service.TensorflowDataService;
import com.timmytime.predictoranalysisplayers.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("tensorflowService")
public class TensorflowDataServiceImpl implements TensorflowDataService {

    private static final Logger log = LoggerFactory.getLogger(TensorflowDataServiceImpl.class);

    private final DateUtils dateUtils = new DateUtils();

    private final PlayerFormRepo playerFormRepo;
    private final ActivePlayersByYearRepo activePlayersByYearRepo;


    @Autowired
    public TensorflowDataServiceImpl(
            PlayerFormRepo playerFormRepo,
            ActivePlayersByYearRepo activePlayersByYearRepo
    ) {
        this.playerFormRepo = playerFormRepo;
        this.activePlayersByYearRepo = activePlayersByYearRepo;
    }

    @Override
    public List<PlayerEventOutcomeCsv> getPlayerCsv(String fromDate, String toDate) {

        log.info("total players {}", playerFormRepo.count());
        activePlayersByYearRepo.findAll()
                .forEach(year -> log.info("we have {} players for {}", year.getPlayers().size(), year.getYear()));


        log.info("getting player data");
        List<PlayerEventOutcomeCsv> playerEventOutcomeCsvs = new ArrayList<>();

        Date from = dateUtils.getDate.apply(fromDate);
        Date to = dateUtils.getDate.apply(toDate);

        Set<UUID> playersToProcess = new HashSet<>();

        Arrays.asList(dateUtils.convertToLocalDate.apply(from).getYear(),dateUtils.convertToLocalDate.apply(to).getYear())
                .stream()
                .forEach(year -> playersToProcess.addAll(activePlayersByYearRepo.findById(year).get().getPlayers()));

        log.info("players to process - {}", playersToProcess.size());
        //we always have, as tensorflow asks by player id..
        playersToProcess.stream().forEach(
                player -> {

                    PlayerForm playerForm = playerFormRepo.findById(player).get();

                    playerForm.getPlayerAppearances()
                            .stream()
                            .filter(f -> f.getDate().equals(from) || f.getDate().after(from))
                            .filter(f -> f.getDate().before(to))
                            .filter(f -> !f.getStatMetrics().isEmpty())
                            .sorted(Comparator.comparing(PlayerAppearance::getDate))
                            .forEach(playerAppearance -> {

                                        PlayerEventOutcomeCsv playerEventOutcomeCsv = new PlayerEventOutcomeCsv();

                                        playerEventOutcomeCsv.setPlayer(playerForm.getId());
                                        playerEventOutcomeCsv.setOpponent(playerAppearance.getOpponent());
                                        playerEventOutcomeCsv.setHome(playerAppearance.getHome() ? "home" : "away");
                                        if (playerAppearance.getDuration().intValue() > 90 || playerAppearance.getDuration().intValue() < 0) {
                                            log.info("player {} has {} minutes", playerForm.getLabel(), playerAppearance.getDuration());
                                            if(playerAppearance.getDuration().intValue() > 90){
                                                playerEventOutcomeCsv.setMinutes(90);
                                            }else if(playerAppearance.getDuration().intValue() <0){
                                                playerEventOutcomeCsv.setMinutes(playerAppearance.getDuration()*-1);
                                            }
                                        } else {
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

                                        playerEventOutcomeCsv.setRed(
                                                playerAppearance.getStatMetrics().stream().filter(f -> f.getEventType().equals(FantasyEventTypes.RED_CARD)).findFirst().orElse(new Event()).getValue());

                                        playerEventOutcomeCsv.setYellow(
                                                playerAppearance.getStatMetrics().stream().filter(f -> f.getEventType().equals(FantasyEventTypes.YELLOW_CARD)).findFirst().orElse(new Event()).getValue());


                                        playerEventOutcomeCsvs.add(playerEventOutcomeCsv);

                                        //note these are not in date order.  which is fine.  i guess.  they for the year.


                                    }
                            );
                }
        );


        log.info("completed player data");

        return playerEventOutcomeCsvs;
    }

}
