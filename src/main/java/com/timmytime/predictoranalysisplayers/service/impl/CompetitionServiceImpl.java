package com.timmytime.predictoranalysisplayers.service.impl;

import com.timmytime.predictoranalysisplayers.callable.Completion;
import com.timmytime.predictoranalysisplayers.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictoranalysisplayers.facade.PlayerFacade;
import com.timmytime.predictoranalysisplayers.receipt.Receipt;
import com.timmytime.predictoranalysisplayers.receipt.ReceiptManager;
import com.timmytime.predictoranalysisplayers.receipt.ReceiptTask;
import com.timmytime.predictoranalysisplayers.service.CompetitionService;
import com.timmytime.predictoranalysisplayers.service.PlayerFormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

@Service("competitionService")
public class CompetitionServiceImpl implements CompetitionService {

    private static final Logger log = LoggerFactory.getLogger(CompetitionServiceImpl.class);

    @Value("${training.receipt.timeout}")
    private Long timeout;

    private final PlayerFacade playerFacade;
    private final PlayerFormService playerFormService;
    private final ReceiptManager receiptManager;

    @Autowired
    public CompetitionServiceImpl(
            PlayerFacade playerFacade,
            PlayerFormService playerFormService,
            ReceiptManager receiptManager
    ) {
        this.playerFacade = playerFacade;
        this.playerFormService = playerFormService;
        this.receiptManager = receiptManager;
    }

    @Override
    public void load(UUID receiptId) {

        playerFormService.clear();

        List<Receipt> receipts = new ArrayList<>();

        Map<Boolean, UUID> receiptIds = new HashMap<>();

        receiptIds.put(Boolean.TRUE, receiptManager.generateId.get());
        receiptIds.put(Boolean.FALSE, receiptManager.generateId.get());

        //this all needs to be receipt controlled as well now.....
        Arrays.asList(ApplicableFantasyLeagues.values())
                .stream()
                .forEach(competition -> {
                    receipts.add(
                            load(competition.name().toLowerCase(), receiptIds.get(Boolean.TRUE), receiptIds.get(Boolean.FALSE))
                    );

                    receiptIds.put(Boolean.TRUE, receiptIds.get(Boolean.FALSE));
                    receiptIds.put(Boolean.FALSE, receiptManager.generateId.get());
                }
                );

        //now need to link the final receipt to a completion receipt.
        receipts.add(
                receiptManager.generateReceipt.apply(
                        receiptIds.get(Boolean.TRUE),
                        new ReceiptTask(new Completion(receiptManager, receiptId),
                        receiptId, timeout)
                )
        );

        receiptManager.sendReceipt.accept(receipts.get(0));
    }

    private Receipt load(String competition, UUID receiptId, UUID nextReceiptId) {

        return receiptManager.generateReceipt.apply(
                receiptId,
                new ReceiptTask(
                        new LoadCompetition(competition, receiptId)
                        , nextReceiptId, timeout
                )
        );


    }



    public class LoadCompetition implements Callable{

        private final String competition;
        private final UUID receiptId;

        public LoadCompetition(
                String competition,
                UUID receiptId
        ){
            this.competition = competition;
            this.receiptId = receiptId;
        }

        @Override
        public Object call() throws Exception {
            log.info("loading {}", competition);

            CompletableFuture.runAsync(() ->
                    playerFacade.getPlayersByCompetition(competition)
                            .stream()
                            .filter(f -> f.getLastAppearance().isAfter(LocalDate.now().minusYears(2L))) //limit player form to last two years
                            .forEach(player -> playerFormService.load(player))
            ).thenRun(() -> {
                log.info("loaded {}", competition);
                receiptManager.receiptReceived.accept(receiptId);
            });

            return null;
        }
    }
}
