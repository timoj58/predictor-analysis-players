package com.timmytime.predictoranalysisplayers.service.impl;


import com.timmytime.predictoranalysisplayers.exception.PlayerNotOnFile;
import com.timmytime.predictoranalysisplayers.facade.MatchFacade;
import com.timmytime.predictoranalysisplayers.model.mongo.PlayersByYear;
import com.timmytime.predictoranalysisplayers.model.redis.PlayerForm;
import com.timmytime.predictoranalysisplayers.repo.mongo.PlayersByYearRepo;
import com.timmytime.predictoranalysisplayers.repo.redis.PlayerFormRepo;
import com.timmytime.predictoranalysisplayers.response.PlayersByTeam;
import com.timmytime.predictoranalysisplayers.response.data.MatchResponse;
import com.timmytime.predictoranalysisplayers.response.data.Player;
import com.timmytime.predictoranalysisplayers.service.PlayerFormService;
import com.timmytime.predictoranalysisplayers.transformer.PlayerAppearanceTransformer;
import com.timmytime.predictoranalysisplayers.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service("playerFormService")
public class PlayerFormServiceImpl implements PlayerFormService {

    private static final Logger log = LoggerFactory.getLogger(PlayerFormServiceImpl.class);

    private final MatchFacade matchFacade;
    private final PlayerFormRepo playerFormRepo;
    private final PlayersByYearRepo playersByYearRepo;
    private final PlayerAppearanceTransformer playerAppearanceTransformer;
    private final DateUtils dateUtils = new DateUtils();

    private Map<Integer, Set<UUID>> activeMap = new HashMap<>();

    BiFunction<List<MatchResponse>, LocalDate, List<MatchResponse>> filterByMonth = (matches, date) ->
            matches
                    .stream()
                    .filter(f -> f.getDate().getYear() == date.getYear() && f.getDate().getMonth() == date.getMonth())
                    .collect(Collectors.toList());

    @Autowired
    public PlayerFormServiceImpl(
            MatchFacade matchFacade,
            PlayerFormRepo playerFormRepo,
            PlayerAppearanceTransformer playerAppearanceTransformer,
            PlayersByYearRepo playersByYearRepo
    ) {
        this.matchFacade = matchFacade;
        this.playerFormRepo = playerFormRepo;
        this.playerAppearanceTransformer = playerAppearanceTransformer;
        this.playersByYearRepo = playersByYearRepo;
    }

    @Override
    public void load(Player player, Boolean firstTime) {

            try {

            PlayerForm playerForm = new PlayerForm(player);

                //due to machine learning, we can not add new players without retraining the model.
                if(firstTime || getPlayers().stream().map(Player::getId).anyMatch(f -> f.equals(player.getId()))) {

                    List<MatchResponse> matches = matchFacade.findByPlayer(player.getId());
                    matches.stream()
                            .forEach(match -> {
                                if (match.getPlayerTeam().equals(player.getLatestTeam())
                                        || (!match.getPlayerTeam().equals(player.getLatestTeam())
                                        && !filterByMonth.apply(matches, match.getDate().toLocalDate())
                                        .stream()
                                        .anyMatch(f -> f.getPlayerTeam().equals(player.getLatestTeam())))
                                ) {

                                    playerForm.getPlayerAppearances().add(
                                            playerAppearanceTransformer.transform.apply(player.getId(), match)
                                    );
                                }
                            });
                    playerFormRepo.save(playerForm);
                    updateActivePlayersByYear(playerForm);
                }
              else {
                  //note: models will need to be retrained due to this, as per the leagues training.  as new records added need to put in
                  log.info("new {} skipped", player.getLabel());
              }
        } catch (Exception e) {
            log.error("player form", e);
        }

    }

    @Override
    public PlayerForm get(UUID id) {
        return playerFormRepo.findById(id).orElseThrow(() -> new PlayerNotOnFile());
    }

    @Override
    public PlayersByTeam getPlayers(UUID team) {

        PlayersByTeam playersByCompetition = new PlayersByTeam();
        playersByCompetition.setTeam(team);
        playersByCompetition.setPlayers(playerFormRepo.findByTeam(team).stream().map(Player::new)
                .filter(f -> f.getLastAppearance().isAfter(LocalDate.now().minusMonths(6)))
                .collect(Collectors.toList()));

        return playersByCompetition;
    }

    @Override
    public List<Player> getPlayers() {
       Set<UUID> players = new HashSet<>();

        playersByYearRepo.findAll()
               .forEach(year-> players.addAll(year.getPlayers()));

       return
               players
               .stream()
               .map(Player::new)
               .collect(Collectors.toList());
    }

    @Override
    public void clear() {
        playerFormRepo.deleteAll();
    }

    @Override
    public Boolean firstTime() {
        IntStream.range(2009, LocalDate.now().plusYears(1).getYear())
                .forEach(year -> activeMap.put(year, new HashSet<>()));

        return playersByYearRepo.count() == 0;
    }

    @Override
    public void saveActiveByYear() {
        activeMap.keySet()
                .stream()
                .forEach(year->
                        playersByYearRepo.findById(year).ifPresentOrElse(
                            then -> {
                                log.info("updating {}", year);
                                then.getPlayers().addAll(activeMap.get(year));
                                playersByYearRepo.save(then);
                            }, () -> {
                                log.info("creating {}", year);
                                PlayersByYear activePlayersByYear = new PlayersByYear();
                                activePlayersByYear.setYear(year);
                                activePlayersByYear.getPlayers().addAll(activeMap.get(year));
                                playersByYearRepo.save(activePlayersByYear);
                            })
                );

        activeMap.clear();

    }


    private void updateActivePlayersByYear(PlayerForm playerForm){

        playerForm
                .getPlayerAppearances()
                .stream()
                .map(m -> dateUtils.convertToLocalDate.apply(m.getDate()).getYear())
                .forEach(year -> activeMap.get(year).add(playerForm.getId()));

    }


}
