package com.timmytime.predictoranalysisplayers.service.impl;

import com.timmytime.predictoranalysisplayers.callable.*;
import com.timmytime.predictoranalysisplayers.receipt.Receipt;
import com.timmytime.predictoranalysisplayers.receipt.ReceiptManager;
import com.timmytime.predictoranalysisplayers.receipt.ReceiptTask;
import com.timmytime.predictoranalysisplayers.service.*;
import com.timmytime.predictoranalysisplayers.util.LambdaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("automationService")
public class AutomationServiceImpl implements AutomationService {

    private static final Logger log = LoggerFactory.getLogger(AutomationServiceImpl.class);

    @Value("${training.receipt.timeout}")
    private Long timeout;

    private final ReceiptManager receiptManager;
    private final CompetitionService competitionService;
    private final TensorflowTrainingService trainingService;
    private final TensorflowPredictionService predictionService;
    private final ValidationService validationService;
    private final PlayerResponseService playerResponseService;
    private final MatchService matchService;
    private final LambdaUtils lambdaUtils;

    @Autowired
    public AutomationServiceImpl(
            ReceiptManager receiptManager,
            CompetitionService competitionService,
            TensorflowTrainingService trainingService,
            TensorflowPredictionService predictionService,
            ValidationService validationService,
            PlayerResponseService playerResponseService,
            MatchService matchService,
            LambdaUtils lambdaUtils
    ) {
        this.receiptManager = receiptManager;
        this.competitionService = competitionService;
        this.trainingService = trainingService;
        this.predictionService = predictionService;
        this.validationService = validationService;
        this.playerResponseService = playerResponseService;
        this.matchService = matchService;
        this.lambdaUtils = lambdaUtils;
    }

    @Override
    public void start() {
        UUID load = receiptManager.generateId.get();
        UUID validate = receiptManager.generateId.get();
       //TODO this cant be done daily.  needs to be seasonal - UUID train = receiptManager.generateId.get();
        UUID predict = receiptManager.generateId.get();
        UUID loadCache = receiptManager.generateId.get();
        UUID loadMatchCache = receiptManager.generateId.get();

        List<Receipt> receipts = new ArrayList<>();

            receipts.add(receiptManager.generateReceipt.apply(
                    load,
                    new ReceiptTask(
                            new Load(competitionService, load),
                            validate
                    )
            ));


            receipts.add(
                    receiptManager.generateReceipt.apply(
                            validate,
                            new ReceiptTask(
                                    new Validate(validationService, validate),
                                    predict)
                    )
            );



        receipts.add(receiptManager.generateReceipt.apply(
                predict,
                new ReceiptTask(
                        new InitPredict(predictionService, predict),
                        loadCache
                )
        ));

        receipts.add(receiptManager.generateReceipt.apply(
                loadCache,
                new ReceiptTask(
                        new LoadPlayerResponseCache(playerResponseService, loadCache),
                        loadMatchCache
                )
        ));

        receipts.add(receiptManager.generateReceipt.apply(
                loadMatchCache,
                new ReceiptTask(
                        new LoadMatchResponseCache(matchService, lambdaUtils, receiptManager, loadMatchCache),
                        null
                )
        ));


        receiptManager.sendReceipt.accept(receipts.get(0));
    }

    @Override
    public void loadLambdaCaches() {

        List<Receipt> receipts = new ArrayList<>();

        UUID loadCache = receiptManager.generateId.get();
        UUID loadMatchCache = receiptManager.generateId.get();

        receipts.add(receiptManager.generateReceipt.apply(
                loadCache,
                new ReceiptTask(
                        new LoadPlayerResponseCache(playerResponseService, loadCache),
                        loadMatchCache,timeout
                )
        ));

        receipts.add(receiptManager.generateReceipt.apply(
                loadMatchCache,
                new ReceiptTask(
                        new LoadMatchResponseCache(matchService, lambdaUtils, receiptManager, loadMatchCache),
                        null,timeout
                )
        ));

        receiptManager.sendReceipt.accept(receipts.get(0));
    }

}
