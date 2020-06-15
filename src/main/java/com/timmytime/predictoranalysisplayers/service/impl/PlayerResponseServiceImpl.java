package com.timmytime.predictoranalysisplayers.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.timmytime.predictoranalysisplayers.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.facade.TeamFacade;
import com.timmytime.predictoranalysisplayers.model.mongo.FantasyOutcome;
import com.timmytime.predictoranalysisplayers.model.redis.Event;
import com.timmytime.predictoranalysisplayers.model.redis.PlayerAppearance;
import com.timmytime.predictoranalysisplayers.model.redis.PlayerForm;
import com.timmytime.predictoranalysisplayers.model.redisson.PlayersResponse;
import com.timmytime.predictoranalysisplayers.receipt.ReceiptManager;
import com.timmytime.predictoranalysisplayers.repo.mongo.FantasyOutcomeRepo;
import com.timmytime.predictoranalysisplayers.repo.redis.PlayerFormRepo;
import com.timmytime.predictoranalysisplayers.repo.redisson.PlayersResponseRepo;
import com.timmytime.predictoranalysisplayers.response.FantasyResponse;
import com.timmytime.predictoranalysisplayers.response.PlayerResponse;
import com.timmytime.predictoranalysisplayers.response.TopPerformerResponse;
import com.timmytime.predictoranalysisplayers.service.PlayerResponseService;
import com.timmytime.predictoranalysisplayers.transformer.FantasyResponseTransformer;
import com.timmytime.predictoranalysisplayers.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service("playerResponseService")
public class PlayerResponseServiceImpl implements PlayerResponseService {

    private static final Logger log = LoggerFactory.getLogger(PlayerResponseServiceImpl.class);

    private final FantasyOutcomeRepo fantasyOutcomeRepo;
    private final PlayerFormRepo playerFormRepo;
    private final TeamFacade teamFacade;
    private final PlayersResponseRepo playersResponseRepo;
    private final ReceiptManager receiptManager;
    private final DateUtils dateUtils = new DateUtils();

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
            PlayersResponseRepo playersResponseRepo,
            TeamFacade teamFacade,
            ReceiptManager receiptManager
    ){
        this.fantasyOutcomeRepo = fantasyOutcomeRepo;
        this.playerFormRepo = playerFormRepo;
        this.playersResponseRepo = playersResponseRepo;
        this.teamFacade = teamFacade;
        this.receiptManager = receiptManager;
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
        if(playerForm.isGoalKeeper()) {
            playerResponse.setSaves(getTotals.apply(playerForm.getPlayerAppearances(), FantasyEventTypes.SAVES));
        }

        //set our current status flags too (historic is worked out in app).
        List<PlayerAppearance> recent = playerForm.getPlayerAppearances()
                .stream()
                .filter(f -> dateUtils.convertToLocalDate.apply(f.getDate()).isAfter(LocalDate.now().minusYears(1)))
                .collect(Collectors.toList());

        if(!recent.isEmpty()) {
            playerResponse.setHardmanYellow((getTotals.apply(recent, FantasyEventTypes.YELLOW_CARD).doubleValue() / (double)recent.size()) * 100.0);
            playerResponse.setHardmanRed((getTotals.apply(recent, FantasyEventTypes.RED_CARD).doubleValue() / (double)recent.size()) * 100.0);
            playerResponse.setWizard( (getTotals.apply(recent, FantasyEventTypes.ASSISTS).doubleValue() / (double)recent.size()) * 100.0);
            playerResponse.setMarksman((getTotals.apply(recent, FantasyEventTypes.GOALS).doubleValue() / (double)recent.size()) * 100.0);
        }

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

    @Override
    public List<TopPerformerResponse> topPerformers(String competition, FantasyEventTypes fantasyEventTypes) {
        List<TopPerformerResponse> topPerformerResponses = new ArrayList<>();
        List<PlayerForm> playerForms = new ArrayList<>();

        teamFacade.getTeamsByCompetition(competition)
                .stream()
                .forEach(team -> playerForms.addAll(playerFormRepo.findByTeam(team.getId())));

        //now find fantasy outcomes by success
        Map<UUID, List<FantasyOutcome>> fantasyOutcomes =
                fantasyOutcomeRepo.findByPlayerIdInAndSuccessAndFantasyEventType(
                        playerForms.stream().map(PlayerForm::getId).collect(Collectors.toList()),
                        Boolean.TRUE,
                        fantasyEventTypes)
                .stream()
                .filter(f -> f.getEventDate().isAfter(LocalDateTime.now().minusWeeks(1)))
                .collect(groupingBy(FantasyOutcome::getPlayerId));


        fantasyOutcomes.keySet().stream()
                .forEach(player -> {

                    PlayerForm playerForm = playerForms.stream().filter(f -> f.getId().equals(player)).findFirst().get();

                    TopPerformerResponse topPerformerResponse = new TopPerformerResponse(playerForm, fantasyEventTypes);

                    topPerformerResponse.setFantasyResponse(fantasyResponseTransformer.transform.apply(fantasyOutcomes.get(player)));

                    topPerformerResponses.add(topPerformerResponse);
                });

        return topPerformerResponses;
    }

    @Override
    public List<TopPerformerResponse> topPicks(String competition, FantasyEventTypes fantasyEventTypes) {
        List<TopPerformerResponse> topPerformerResponses = new ArrayList<>();
        List<PlayerForm> playerForms = new ArrayList<>();

        teamFacade.getTeamsByCompetition(competition)
                .stream()
                .forEach(team -> playerForms.addAll(playerFormRepo.findByTeam(team.getId())));
        return null;
    }

    @Override
    public void load(UUID receipt) {

        log.info("loading player responses");

        Arrays.asList(ApplicableFantasyLeagues.values())
                .stream()
                .forEach(competition ->
                        teamFacade.getTeamsByCompetition(competition.name().toLowerCase())
                .stream()
                .forEach(team ->
                        {
                            playersResponseRepo.deleteAll(team.getId().toString());

                            List<PlayerResponse> playerResponses = new ArrayList<>();
                            playerFormRepo.findByTeam(team.getId()).stream().forEach(player -> playerResponses.add(get(player.getId())));

                            try {
                                playersResponseRepo.save(team.getId().toString(), new PlayersResponse(playerResponses));
                            } catch (JsonProcessingException e) {
                                log.error("teams players response cache", e);
                            }

                        }));


        receiptManager.receiptReceived.accept(receipt);
    }


}

