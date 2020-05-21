package com.timmytime.predictoranalysisplayers.service.impl;

import com.timmytime.predictoranalysisplayers.callable.InitPredict;
import com.timmytime.predictoranalysisplayers.callable.InitTrain;
import com.timmytime.predictoranalysisplayers.callable.Load;
import com.timmytime.predictoranalysisplayers.callable.Validate;
import com.timmytime.predictoranalysisplayers.receipt.Receipt;
import com.timmytime.predictoranalysisplayers.receipt.ReceiptManager;
import com.timmytime.predictoranalysisplayers.receipt.ReceiptTask;
import com.timmytime.predictoranalysisplayers.service.*;
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

    @Autowired
    public AutomationServiceImpl(
            ReceiptManager receiptManager,
            CompetitionService competitionService,
            TensorflowTrainingService trainingService,
            TensorflowPredictionService predictionService,
            ValidationService validationService
    ) {
        this.receiptManager = receiptManager;
        this.competitionService = competitionService;
        this.trainingService = trainingService;
        this.predictionService = predictionService;
        this.validationService = validationService;
    }

    @Override
    public void start() {
        UUID load = receiptManager.generateId.get();
        UUID validate = receiptManager.generateId.get();
        UUID train = receiptManager.generateId.get();
        UUID predict = receiptManager.generateId.get();

        List<Receipt> receipts = new ArrayList<>();

        receipts.add(receiptManager.generateReceipt.apply(
                load,
                new ReceiptTask(
                        new Load(competitionService, load),
                        validate,timeout
                )
        ));


        receipts.add(
                receiptManager.generateReceipt.apply(
                        validate,
                        new ReceiptTask(
                                new Validate(validationService,validate),
                                train, timeout)
                )
        );



        receipts.add(receiptManager.generateReceipt.apply(
                train,
                new ReceiptTask(
                        new InitTrain(trainingService, train),
                        predict,timeout
                )
        ));


        receipts.add(receiptManager.generateReceipt.apply(
                predict,
                new ReceiptTask(
                        new InitPredict(predictionService, predict),
                        null,timeout
                )
        ));

        /*
         note the final receipt will be a lambda to turn machine learning off.  TODO
         */


        receiptManager.sendReceipt.accept(receipts.get(0));
    }

}
