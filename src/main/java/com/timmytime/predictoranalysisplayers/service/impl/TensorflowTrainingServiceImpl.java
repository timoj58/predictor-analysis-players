package com.timmytime.predictoranalysisplayers.service.impl;

import com.timmytime.predictoranalysisplayers.callable.Train;
import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.facade.TeamFacade;
import com.timmytime.predictoranalysisplayers.facade.TensorflowFacade;
import com.timmytime.predictoranalysisplayers.receipt.Receipt;
import com.timmytime.predictoranalysisplayers.receipt.ReceiptManager;
import com.timmytime.predictoranalysisplayers.receipt.ReceiptTask;
import com.timmytime.predictoranalysisplayers.repo.redis.PlayerFormRepo;
import com.timmytime.predictoranalysisplayers.service.TensorflowTrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("tensorflowTrainingService")
public class TensorflowTrainingServiceImpl implements TensorflowTrainingService {

    private static final Logger log = LoggerFactory.getLogger(TensorflowTrainingServiceImpl.class);

    @Value("${training.receipt.timeout}")
    private Long timeout;

    private final TeamFacade teamFacade;
    private final PlayerFormRepo playerFormRepo;
    private final TensorflowFacade tensorflowFacade;
    private final ReceiptManager receiptManager;

    @Autowired
    public TensorflowTrainingServiceImpl(
            TeamFacade teamFacade,
            PlayerFormRepo playerFormRepo,
            TensorflowFacade tensorflowFacade,
            ReceiptManager receiptManager
    ) {
        this.teamFacade = teamFacade;
        this.playerFormRepo = playerFormRepo;
        this.tensorflowFacade = tensorflowFacade;
        this.receiptManager = receiptManager;
    }
/*
    @Override
    public void train(UUID receipt) {

        List<Receipt> receipts = new ArrayList<>();

        Map<Boolean, UUID> receiptIds = new HashMap<>();
        receiptIds.put(Boolean.TRUE, receiptManager.generateId.get());
        receiptIds.put(Boolean.FALSE, receiptManager.generateId.get());


                        Arrays.asList(ApplicableFantasyLeagues.values())
                                .stream()
                                .filter(f -> competition.isEmpty() || f.name().toLowerCase().equals(competition.get()))
                                .forEach(comp ->
                                        teamFacade.getTeamsByCompetition(comp.name().toLowerCase())
                                                .stream()
                                                .forEach(team ->
                                                        playerFormRepo.findByTeam(team.getId())
                                                                .stream()
                                                                .forEach(player ->
                                                                    Arrays.asList(FantasyEventTypes.values())
                                                                            .stream()
                                                                            .filter(f -> f.getPredict())
                                                                            .forEach(fantasyEventTypes -> {

                                                                                    //only for GK do we do saves
                                                                    //actually at this point need to create all the receipts...and use receipts to execute.
                                                                    if(fantasyEventTypes.equals(FantasyEventTypes.SAVES) && player.isGoalKeeper()
                                                                     || !fantasyEventTypes.equals(FantasyEventTypes.SAVES)) {
                                                                        log.info("training {} {} - {}", player.getLabel(), team.getLabel(),fantasyEventTypes.name());
                                                                        receipts.add(create(player.getId(), fantasyEventTypes, receiptIds.get(Boolean.TRUE), receiptIds.get(Boolean.FALSE)));
                                                                        receiptIds.put(Boolean.TRUE, receiptIds.get(Boolean.FALSE));
                                                                        receiptIds.put(Boolean.FALSE, receiptManager.generateId.get());
                                                                    }
                                                                })
                                                                )
                                                )
                                );

        //now start the first receipt. (if we have)
        if(!receipts.isEmpty()) {
            receiptManager.sendReceipt.accept(receipts.get(0));
        }

    }
    */

    @Override
    public void train(UUID receipt) {
        //one large training set now...to review it.
        List<Receipt> receipts = new ArrayList<>();

        Map<Boolean, UUID> receiptIds = new HashMap<>();
        receiptIds.put(Boolean.TRUE, receiptManager.generateId.get());
        receiptIds.put(Boolean.FALSE, receiptManager.generateId.get());


        Arrays.asList(FantasyEventTypes.values())
                .stream()
                .filter(f -> f.getPredict())
                .forEach(fantasyEventTypes -> {
                        log.info("training {}",fantasyEventTypes.name());
                        receipts.add(create(fantasyEventTypes, receiptIds.get(Boolean.TRUE), receiptIds.get(Boolean.FALSE)));
                        receiptIds.put(Boolean.TRUE, receiptIds.get(Boolean.FALSE));
                        receiptIds.put(Boolean.FALSE, receiptManager.generateId.get());
                });

        //now start the first receipt. (if we have)
        if(!receipts.isEmpty()) {
            receiptManager.sendReceipt.accept(receipts.get(0));
        }

    }

    @Override
    public void receiveReceipt(UUID receipt) {
        receiptManager.receiptReceived.accept(receipt);
    }

    private Receipt create(FantasyEventTypes fantasyEventTypes, UUID receiptId, UUID nextReceiptId){


        return receiptManager.generateReceipt.apply(
                receiptId,
                new ReceiptTask(
                        new Train(
                                tensorflowFacade,
                                fantasyEventTypes,
                                receiptId
                        ),
                        nextReceiptId, timeout)
        );

    };

}
