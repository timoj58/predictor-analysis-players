package com.timmytime.predictoranalysisplayers.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictoranalysisplayers.response.PlayerResponse;
import com.timmytime.predictoranalysisplayers.service.PlayerResponseService;
import com.timmytime.predictoranalysisplayers.service.TensorflowPredictionService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/prediction/players/predict")
public class TensorflowPredictController {

    private static final Logger log = LoggerFactory.getLogger(TensorflowPredictController.class);

    @Autowired
    private TensorflowPredictionService tensorflowPredictionService;
    @Autowired
    private PlayerResponseService playerResponseService;

    @RequestMapping(
            value = "receipt",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_AUTOMATION')")
    public void predictReceipt(
            @RequestParam("id") UUID id,
            @RequestBody JsonNode results) {

        log.info("received following result {}", results.toString());
        tensorflowPredictionService.receiveReceipt(new JSONObject(results.toString()), id);
    }


    @RequestMapping(
            value = "",
            method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_AUTOMATION')")
    public void eventPredictions(){

        tensorflowPredictionService.predict(UUID.randomUUID());
    }



    //note mainly for testing, will be in redis etc for the mobile app find gateway / lambda
    @RequestMapping(
            value = "{player-id}",
            method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_AUTOMATION')")
    public ResponseEntity<PlayerResponse> latestMatchResponse(
            @PathVariable("player-id") UUID playerId) {
        return ResponseEntity.ok(playerResponseService.get(playerId));
    }

}
