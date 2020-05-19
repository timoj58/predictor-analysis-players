package com.timmytime.predictoranalysisplayers.controller;


import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.service.TensorflowTrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/prediction/players/train")
public class TensorflowTrainController {

    private static final Logger log = LoggerFactory.getLogger(TensorflowTrainController.class);


    @Autowired
    private TensorflowTrainingService tensorflowTrainingService;

    @RequestMapping(
            value = "receipt",
            method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_AUTOMATION')")
    public void trainReceipt(
            @RequestParam("id") UUID id) {

        log.info("received {}", id.toString());
        tensorflowTrainingService.receiveReceipt(id);

    }



    @RequestMapping(
            value = "",
            method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_AUTOMATION')")
    public void trainAll() {
        CompletableFuture.runAsync(() -> tensorflowTrainingService.train(UUID.randomUUID()));
    }
}
