package com.timmytime.predictoranalysisplayers.service.impl;

import com.timmytime.predictoranalysisplayers.callable.InitPredict;
import com.timmytime.predictoranalysisplayers.callable.InitTrain;
import com.timmytime.predictoranalysisplayers.callable.Load;
import com.timmytime.predictoranalysisplayers.callable.Validate;
import com.timmytime.predictoranalysisplayers.receipt.Receipt;
import com.timmytime.predictoranalysisplayers.receipt.ReceiptManager;
import com.timmytime.predictoranalysisplayers.receipt.ReceiptTask;
import com.timmytime.predictoranalysisplayers.service.AutomationService;
import com.timmytime.predictoranalysisplayers.service.CompetitionService;
import com.timmytime.predictoranalysisplayers.service.TensorflowPredictionService;
import com.timmytime.predictoranalysisplayers.service.TensorflowTrainingService;
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

    @Autowired
    public AutomationServiceImpl(
            ReceiptManager receiptManager,
            CompetitionService competitionService,
            TensorflowTrainingService trainingService,
            TensorflowPredictionService predictionService
    ) {
        this.receiptManager = receiptManager;
        this.competitionService = competitionService;
        this.trainingService = trainingService;
        this.predictionService = predictionService;
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

        /*
         need to do the validation one here.. TODO
         */
       /* receipts.add(
                receiptManager.generateReceipt.apply(
                        validate,
                        new ReceiptTask(new Validate(), train, timeout)
                )
        );
        */


        //remove for timebeing.  test the other stuff first...

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


        receiptManager.sendReceipt.accept(receipts.get(0));
    }

}
