package com.timmytime.predictoranalysisplayers.service.impl;


import com.timmytime.predictoranalysisplayers.exception.PlayerNotOnFile;
import com.timmytime.predictoranalysisplayers.facade.MatchFacade;
import com.timmytime.predictoranalysisplayers.model.redis.PlayerForm;
import com.timmytime.predictoranalysisplayers.repo.redis.PlayerFormRepo;
import com.timmytime.predictoranalysisplayers.response.PlayersByTeam;
import com.timmytime.predictoranalysisplayers.response.data.MatchResponse;
import com.timmytime.predictoranalysisplayers.response.data.Player;
import com.timmytime.predictoranalysisplayers.service.PlayerFormService;
import com.timmytime.predictoranalysisplayers.transformer.PlayerAppearanceTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service("playerFormService")
public class PlayerFormServiceImpl implements PlayerFormService {

    private static final Logger log = LoggerFactory.getLogger(PlayerFormServiceImpl.class);

    private final MatchFacade matchFacade;
    private final PlayerFormRepo playerFormRepo;
    private final PlayerAppearanceTransformer playerAppearanceTransformer;

    BiFunction<List<MatchResponse>, LocalDate, List<MatchResponse>> filterByMonth = (matches, date) ->
            matches
                    .stream()
                    .filter(f -> f.getDate().getYear() == date.getYear() && f.getDate().getMonth() == date.getMonth())
                    .collect(Collectors.toList());

    @Autowired
    public PlayerFormServiceImpl(
            MatchFacade matchFacade,
            PlayerFormRepo playerFormRepo,
            PlayerAppearanceTransformer playerAppearanceTransformer
    ) {
        this.matchFacade = matchFacade;
        this.playerFormRepo = playerFormRepo;
        this.playerAppearanceTransformer = playerAppearanceTransformer;
    }

    @Override
    public void load(Player player, Boolean firstTime) {

            try {

            PlayerForm playerForm = new PlayerForm(player);

                //due to machine learning, we can not add new players without retraining the model.
                if(firstTime || playerFormRepo.findById(playerForm.getId()).isPresent()) {

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
                }
              else {
                  //note: models will need to be retrained due to this, as per the leagues training.  as new records added need to put in
                  log.info("new player skipped - this needs to be logged in mongo longterm,  and retrain once a sufficient number");
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
        playersByCompetition.setPlayers(playerFormRepo.findByTeam(team).stream().map(PlayerForm::getId).collect(Collectors.toList()));

        return playersByCompetition;
    }

    @Override
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        playerFormRepo.findAll()
                .forEach(pf -> {
                    Player player = new Player();
                    player.setId(pf.getId());

                    players.add(player);
                });
        return players;
    }

    @Override
    public void clear() {
        playerFormRepo.deleteAll();
    }

    @Override
    public Boolean firstTime() {
        return playerFormRepo.count() == 0;
    }

}
