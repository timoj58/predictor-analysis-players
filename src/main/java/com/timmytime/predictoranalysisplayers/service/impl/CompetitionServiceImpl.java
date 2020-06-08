package com.timmytime.predictoranalysisplayers.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.timmytime.predictoranalysisplayers.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictoranalysisplayers.facade.EventFacade;
import com.timmytime.predictoranalysisplayers.facade.PlayerFacade;
import com.timmytime.predictoranalysisplayers.facade.TeamFacade;
import com.timmytime.predictoranalysisplayers.model.redisson.CompetitionTeamsResponse;
import com.timmytime.predictoranalysisplayers.model.redisson.MatchSelectionsResponse;
import com.timmytime.predictoranalysisplayers.receipt.ReceiptManager;
import com.timmytime.predictoranalysisplayers.repo.redisson.CompetitionTeamsResponseRepo;
import com.timmytime.predictoranalysisplayers.repo.redisson.MatchSelectionsResponseRepo;
import com.timmytime.predictoranalysisplayers.response.MatchPrediction;
import com.timmytime.predictoranalysisplayers.response.data.Player;
import com.timmytime.predictoranalysisplayers.response.data.Team;
import com.timmytime.predictoranalysisplayers.service.CompetitionService;
import com.timmytime.predictoranalysisplayers.service.PlayerFormService;
import com.timmytime.predictoranalysisplayers.service.PlayerResponseService;
import com.timmytime.predictoranalysisplayers.transformer.MatchSelectionResponseTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Service("competitionService")
public class CompetitionServiceImpl implements CompetitionService {

    private static final Logger log = LoggerFactory.getLogger(CompetitionServiceImpl.class);

    @Value("${training.receipt.timeout}")
    private Long timeout;

    private final PlayerFacade playerFacade;
    private final TeamFacade teamFacade;
    private final PlayerFormService playerFormService;
    private final ReceiptManager receiptManager;
    private final PlayerResponseService playerResponseService;
    private final EventFacade eventFacade;
    private final CompetitionTeamsResponseRepo competitionTeamsResponseRepo;
    private final MatchSelectionsResponseRepo matchSelectionsResponseRepo;

    private final MatchSelectionResponseTransformer matchSelectionResponseTransformer = new MatchSelectionResponseTransformer();

    private Map<String, Boolean> loadingStatus = new HashMap<>();

    @Autowired
    public CompetitionServiceImpl(
            PlayerFacade playerFacade,
            TeamFacade teamFacade,
            PlayerFormService playerFormService,
            PlayerResponseService playerResponseService,
            CompetitionTeamsResponseRepo competitionTeamsResponseRepo,
            EventFacade eventFacade,
            MatchSelectionsResponseRepo matchSelectionsResponseRepo,
            ReceiptManager receiptManager
    ) {
        this.playerFacade = playerFacade;
        this.teamFacade = teamFacade;
        this.playerFormService = playerFormService;
        this.playerResponseService = playerResponseService;
        this.eventFacade = eventFacade;
        this.competitionTeamsResponseRepo = competitionTeamsResponseRepo;
        this.matchSelectionsResponseRepo = matchSelectionsResponseRepo;
        this.receiptManager = receiptManager;
    }

    @Override
    public void load(UUID receiptId) {

        loadingStatus.clear();
        Boolean firstTime = playerFormService.firstTime();
        playerFormService.clear();


        //this all needs to be receipt controlled as well now.....
        Arrays.asList(ApplicableFantasyLeagues.values())
                .stream()
                .forEach(competition -> {
                            log.info("loading {}", competition);
                            loadingStatus.put(competition.name(), Boolean.TRUE);

                            CompletableFuture.runAsync(() ->
                                    playerFacade.getPlayersByCompetition(competition.name().toLowerCase())
                                            .stream()
                                            .filter(f -> f.getLastAppearance().isAfter(LocalDate.now().minusYears(2L))) //limit player form to last two years
                                            .forEach(player -> playerFormService.load(player, firstTime))
                            ).thenRun(() -> {
                                log.info("loaded {}", competition);
                                loadingStatus.put(competition.name(), Boolean.FALSE);
                            });
                }
                );

        new CompetitionWatcher(() -> loadingStatus.values().stream().allMatch(f -> f == Boolean.FALSE), receiptId).start();

    }

    @Override
    public MatchPrediction get(UUID home, UUID away) {
        MatchPrediction matchPrediction = new MatchPrediction();

        matchPrediction.setHomeLabel(teamFacade.findById(home).get().getLabel());
        matchPrediction.setAwayLabel(teamFacade.findById(away).get().getLabel());


        playerFormService.getPlayers(home).getPlayers()
                .stream()
                .forEach(player -> matchPrediction.getHomePlayers().add(playerResponseService.get(player.getId())));

        playerFormService.getPlayers(away).getPlayers()
                .stream()
                .forEach(player -> matchPrediction.getAwayPlayers().add(playerResponseService.get(player.getId())));

        return matchPrediction;
    }

    @Override
    @PostConstruct //remove this.. for now while testing...needs to be part of end process.
    public void loadMatches() {
        //load the top picks to redisson cache for matches.
        Arrays.asList(ApplicableFantasyLeagues.values())
                .stream()
                .forEach(league -> {

                    matchSelectionsResponseRepo.deleteAll(league.name().toLowerCase());
                    eventFacade.upcomingEvents(league.getCountry(), league.name().toLowerCase())
                            .stream()
                            .forEach(events -> events.getUpcomingEventResponses()
                                    .stream()
                                    .forEach(event -> {

                                                try {
                                                    matchSelectionsResponseRepo.save(
                                                            league.name().toLowerCase(),
                                                            new MatchSelectionsResponse(
                                                                    event.getHome().getId(),
                                                                    event.getAway().getId(),
                                                                    matchSelectionResponseTransformer.transform.apply(
                                                                            get(
                                                                                    event.getHome().getId(),
                                                                                    event.getAway().getId())
                                                                    )
                                                            )
                                                    );
                                                } catch (JsonProcessingException e) {
                                                    log.error("match selections", e);
                                                }
                                            }
                                    )
                            );
                });

    }


    @PostConstruct
    private void loadTeams(){

        competitionTeamsResponseRepo.deleteAll();

        Arrays.asList(ApplicableFantasyLeagues.values())
                .stream()
                .forEach(league ->
                {

                    List<Team> teams = teamFacade.getTeamsByCompetition(league.name().toLowerCase());

                    try {
                        competitionTeamsResponseRepo.save(
                                new CompetitionTeamsResponse(
                                        league.name().toLowerCase(),
                                        teams
                                )
                        );
                    } catch (JsonProcessingException e) {
                        log.error("teams cache", e);
                    }
                });
    }


    private class CompetitionWatcher implements Runnable {

        private Thread worker;
        private UUID receipt;
        private Supplier<Boolean> supplier;


        public CompetitionWatcher(Supplier<Boolean> supplier, UUID receipt) {
            this.receipt = receipt;
            this.supplier = supplier;
            worker = new Thread(this);
        }

        public void start() {
            worker.start();
        }

        @Override
        public void run() {

            while (!supplier.get()) {
                try {
                    waitFor(90000L);
                } catch (InterruptedException e) {
                    log.error("competitions watcher", e);
                }
            }

            log.info("competitions finished loading");

            playerFormService.saveActiveByYear();
            receiptManager.receiptReceived.accept(receipt);
        }

        private synchronized void waitFor(long timeout) throws InterruptedException {
            log.info("waiting for competitions...");
            wait(timeout);
        }

    }

}
