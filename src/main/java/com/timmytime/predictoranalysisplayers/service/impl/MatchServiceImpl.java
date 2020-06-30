package com.timmytime.predictoranalysisplayers.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.timmytime.predictoranalysisplayers.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.facade.EventFacade;
import com.timmytime.predictoranalysisplayers.facade.TeamFacade;
import com.timmytime.predictoranalysisplayers.model.redisson.MatchSelectionsResponse;
import com.timmytime.predictoranalysisplayers.model.redisson.TopSelectionsResponse;
import com.timmytime.predictoranalysisplayers.receipt.ReceiptManager;
import com.timmytime.predictoranalysisplayers.repo.redisson.IRedissonRepo;
import com.timmytime.predictoranalysisplayers.response.MatchPrediction;
import com.timmytime.predictoranalysisplayers.response.MatchSelectionResponse;
import com.timmytime.predictoranalysisplayers.response.PlayerResponse;
import com.timmytime.predictoranalysisplayers.service.MatchService;
import com.timmytime.predictoranalysisplayers.service.PlayerFormService;
import com.timmytime.predictoranalysisplayers.service.PlayerResponseService;
import com.timmytime.predictoranalysisplayers.transformer.MatchSelectionResponseTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

@Service("matchService")
public class MatchServiceImpl implements MatchService {

    private static final Logger log = LoggerFactory.getLogger(MatchServiceImpl.class);

    private final TeamFacade teamFacade;
    private final EventFacade eventFacade;
    private final PlayerFormService playerFormService;
    private final PlayerResponseService playerResponseService;
    private final IRedissonRepo matchSelectionsResponseRepo;
    private final IRedissonRepo topSelectionsResponseRepo;

    private final MatchSelectionResponseTransformer matchSelectionResponseTransformer = new MatchSelectionResponseTransformer();

    private BiFunction<MatchSelectionsResponse, FantasyEventTypes, List<PlayerResponse>> process = (matchSelectionsResponse, fantasyEventTypes) ->
            matchSelectionsResponse.getMatchSelectionResponses()
                    .stream()
                    .filter(f -> f.getEvent().equals(fantasyEventTypes.name().toLowerCase()))
                    .findFirst()
                    .orElse(new MatchSelectionResponse()).getPlayerResponses();

    @Autowired
    public MatchServiceImpl(
            TeamFacade teamFacade,
            EventFacade eventFacade,
            PlayerFormService playerFormService,
            PlayerResponseService playerResponseService,
            IRedissonRepo matchSelectionsResponseRepo,
            IRedissonRepo topSelectionsResponseRepo
            ){
        this.teamFacade = teamFacade;
        this.eventFacade = eventFacade;
        this.playerFormService = playerFormService;
        this.playerResponseService = playerResponseService;
        this.matchSelectionsResponseRepo = matchSelectionsResponseRepo;
        this.topSelectionsResponseRepo = topSelectionsResponseRepo;
    }

    @Override
    public MatchPrediction get(UUID home, UUID away) {
        MatchPrediction matchPrediction = new MatchPrediction();

        matchPrediction.setHomeLabel(teamFacade.findById(home).get().getLabel());
        matchPrediction.setAwayLabel(teamFacade.findById(away).get().getLabel());

        playerFormService.getPlayers(home).getPlayers()
                .forEach(player -> matchPrediction.getHomePlayers().add(playerResponseService.get(player.getId())));

        playerFormService.getPlayers(away).getPlayers()
                .forEach(player -> matchPrediction.getAwayPlayers().add(playerResponseService.get(player.getId())));
        return matchPrediction;
    }

    @Override
    public void loadMatches() {
        log.info("loading match responses");
        //load the top picks to redisson cache for matches.
        Arrays.asList(ApplicableFantasyLeagues.values())
                .stream()
                .forEach(league -> {

                    //also need to work the top picks...keeping 20 per event
                    TopSelectionsResponse topSelectionsGoalsResponse = new TopSelectionsResponse(FantasyEventTypes.GOALS, new ArrayList<>()); //should be using enums...
                    TopSelectionsResponse topSelectionsAssistsResponse = new TopSelectionsResponse(FantasyEventTypes.ASSISTS, new ArrayList<>());
                    TopSelectionsResponse topSelectionsSavesResponse = new TopSelectionsResponse(FantasyEventTypes.SAVES, new ArrayList<>());
                    TopSelectionsResponse topSelectionsYellowsResponse = new TopSelectionsResponse(FantasyEventTypes.YELLOW_CARD, new ArrayList<>());

                    matchSelectionsResponseRepo.deleteAll(league.name().toLowerCase());
                    topSelectionsResponseRepo.deleteAll(league.name().toLowerCase());

                    eventFacade.upcomingEvents(league.getCountry(), league.name().toLowerCase())
                            .stream()
                            .forEach(events -> events.getUpcomingEventResponses()
                                    .stream()
                                    .forEach(event -> {

                                                log.info("processing {} vs {}", event.getHome().getLabel(), event.getAway().getLabel());

                                                MatchSelectionsResponse matchSelectionsResponse
                                                        =   new MatchSelectionsResponse(
                                                        event.getHome().getId(),
                                                        event.getAway().getId(),
                                                        matchSelectionResponseTransformer.transform.apply(
                                                                get(
                                                                        event.getHome().getId(),
                                                                        event.getAway().getId())
                                                        )
                                                );

                                                //process top selections
                                                topSelectionsGoalsResponse.process(process.apply(matchSelectionsResponse, FantasyEventTypes.GOALS));
                                                topSelectionsAssistsResponse.process(process.apply(matchSelectionsResponse, FantasyEventTypes.ASSISTS));
                                                topSelectionsSavesResponse.process(process.apply(matchSelectionsResponse, FantasyEventTypes.SAVES));
                                                topSelectionsYellowsResponse.process(process.apply(matchSelectionsResponse, FantasyEventTypes.YELLOW_CARD));


                                                try {
                                                    matchSelectionsResponseRepo.save(
                                                            league.name().toLowerCase(),
                                                            matchSelectionsResponse
                                                    );
                                                } catch (JsonProcessingException e) {
                                                    log.error("match selections", e);
                                                } catch (Exception e){
                                                    log.error("something else", e);
                                                }
                                            }
                                    )
                            );

                    //save top selections
                    try {
                        topSelectionsResponseRepo.save(league.name().toLowerCase(), topSelectionsGoalsResponse);
                        topSelectionsResponseRepo.save(league.name().toLowerCase(), topSelectionsAssistsResponse);
                        topSelectionsResponseRepo.save(league.name().toLowerCase(), topSelectionsSavesResponse);
                        topSelectionsResponseRepo.save(league.name().toLowerCase(), topSelectionsYellowsResponse);

                    } catch (JsonProcessingException e) {
                        log.error("match selections", e);
                    } catch (Exception e){
                        log.error("something else", e);
                    }
                });

    }

}
