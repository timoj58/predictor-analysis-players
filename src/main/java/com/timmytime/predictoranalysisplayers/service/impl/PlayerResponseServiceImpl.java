package com.timmytime.predictoranalysisplayers.service.impl;

import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.facade.TeamFacade;
import com.timmytime.predictoranalysisplayers.model.mongo.FantasyOutcome;
import com.timmytime.predictoranalysisplayers.model.redis.Event;
import com.timmytime.predictoranalysisplayers.model.redis.PlayerAppearance;
import com.timmytime.predictoranalysisplayers.model.redis.PlayerForm;
import com.timmytime.predictoranalysisplayers.repo.mongo.FantasyOutcomeRepo;
import com.timmytime.predictoranalysisplayers.repo.redis.PlayerFormRepo;
import com.timmytime.predictoranalysisplayers.response.FantasyResponse;
import com.timmytime.predictoranalysisplayers.response.PlayerResponse;
import com.timmytime.predictoranalysisplayers.response.data.StatMetric;
import com.timmytime.predictoranalysisplayers.service.PlayerResponseService;
import com.timmytime.predictoranalysisplayers.transformer.FantasyResponseTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;

@Service("playerResponseService")
public class PlayerResponseServiceImpl implements PlayerResponseService {


    private final FantasyOutcomeRepo fantasyOutcomeRepo;
    private final PlayerFormRepo playerFormRepo;
    private final TeamFacade teamFacade;

    private final FantasyResponseTransformer fantasyResponseTransformer = new FantasyResponseTransformer();


    BiFunction<List<PlayerAppearance>, FantasyEventTypes, Integer> getTotals = (playerAppearances, fantasyEventTypes) -> {

        List<Event> statMetrics = new ArrayList<>();

        playerAppearances.stream().forEach(playerAppearance -> statMetrics.addAll(playerAppearance.getStatMetrics()));

        return statMetrics.stream().filter(f -> f.getEventType().equals(fantasyEventTypes)).mapToInt(m -> m.getValue()).sum();
    };


    @Autowired
    public PlayerResponseServiceImpl(
            FantasyOutcomeRepo fantasyOutcomeRepo,
            PlayerFormRepo playerFormRepo,
            TeamFacade teamFacade
    ){
        this.fantasyOutcomeRepo = fantasyOutcomeRepo;
        this.playerFormRepo = playerFormRepo;
        this.teamFacade = teamFacade;
    }

    @Override
    public PlayerResponse get(UUID playerId) {

        PlayerForm playerForm = playerFormRepo.findById(playerId).get();
        List<FantasyOutcome> fantasyOutcomes = fantasyOutcomeRepo.findByPlayerIdAndSuccessNull(playerId);

        PlayerResponse playerResponse = new PlayerResponse();
        playerResponse.setId(playerId);
        playerResponse.setLabel(playerForm.getLabel());
        playerResponse.setCurrentTeam(teamFacade.findById(playerForm.getTeam()).get().getLabel());

        //also need to work out, set
        playerResponse.setAppearances(
                playerForm.getPlayerAppearances().size()
        );
        //need to sort these out at some point.  needs util to do it.
        playerResponse.setGoals(getTotals.apply(playerForm.getPlayerAppearances(), FantasyEventTypes.GOALS));
        playerResponse.setAssists(getTotals.apply(playerForm.getPlayerAppearances(), FantasyEventTypes.ASSISTS));
        playerResponse.setRedCards(getTotals.apply(playerForm.getPlayerAppearances(), FantasyEventTypes.RED_CARD));
        playerResponse.setYellowCards(getTotals.apply(playerForm.getPlayerAppearances(), FantasyEventTypes.YELLOW_CARD));

        if(!fantasyOutcomes.isEmpty()) {

            fantasyOutcomes.stream().collect(groupingBy(FantasyOutcome::getOpponent))
                    .values()
                    .stream()
                    .forEach(match -> {
                        FantasyResponse fantasyResponse = fantasyResponseTransformer.transform.apply(match);

                        UUID opponent = match.stream().map(m -> m.getOpponent()).distinct().findFirst().get();
                        Boolean isHome = match.stream().map(m -> m.getHome()).distinct().findFirst().get().contentEquals("home");

                        fantasyResponse.setOpponent(teamFacade.findById(opponent).get().getLabel());
                        fantasyResponse.setIsHome(isHome);

                        playerResponse.getFantasyResponse().add(fantasyResponse);
                    });


        }

        return playerResponse;
    }


}
