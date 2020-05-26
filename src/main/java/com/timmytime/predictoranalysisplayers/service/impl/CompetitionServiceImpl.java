package com.timmytime.predictoranalysisplayers.service.impl;

import com.timmytime.predictoranalysisplayers.callable.Completion;
import com.timmytime.predictoranalysisplayers.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictoranalysisplayers.facade.PlayerFacade;
import com.timmytime.predictoranalysisplayers.facade.TeamFacade;
import com.timmytime.predictoranalysisplayers.receipt.Receipt;
import com.timmytime.predictoranalysisplayers.receipt.ReceiptManager;
import com.timmytime.predictoranalysisplayers.receipt.ReceiptTask;
import com.timmytime.predictoranalysisplayers.response.MatchPrediction;
import com.timmytime.predictoranalysisplayers.service.CompetitionService;
import com.timmytime.predictoranalysisplayers.service.PlayerFormService;
import com.timmytime.predictoranalysisplayers.service.PlayerResponseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Callable;
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

    private Map<String, Boolean> loadingStatus = new HashMap<>();

    @Autowired
    public CompetitionServiceImpl(
            PlayerFacade playerFacade,
            TeamFacade teamFacade,
            PlayerFormService playerFormService,
            PlayerResponseService playerResponseService,
            ReceiptManager receiptManager
    ) {
        this.playerFacade = playerFacade;
        this.teamFacade = teamFacade;
        this.playerFormService = playerFormService;
        this.playerResponseService = playerResponseService;
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
                .forEach(player -> matchPrediction.getHomePlayers().add(playerResponseService.get(player)));

        playerFormService.getPlayers(away).getPlayers()
                .stream()
                .forEach(player -> matchPrediction.getAwayPlayers().add(playerResponseService.get(player)));

        return matchPrediction;
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
