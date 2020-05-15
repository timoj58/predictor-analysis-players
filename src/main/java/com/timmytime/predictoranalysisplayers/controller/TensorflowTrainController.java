package com.timmytime.predictoranalysisplayers.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.service.TensorflowTrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
            value = "{player-id}",
            method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_AUTOMATION')")
    public void train(
            @PathVariable("player-id") UUID playerId,
            @RequestParam("eventType") String eventType) {
       tensorflowTrainingService.trainPlayer(playerId, FantasyEventTypes.valueOf(eventType));
    }



    @RequestMapping(
            value = "",
            method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_AUTOMATION')")
    public void trainAll() {
        CompletableFuture.runAsync(() -> tensorflowTrainingService.train(UUID.randomUUID()));
    }
}
