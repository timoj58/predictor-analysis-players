package com.timmytime.predictoranalysisplayers.controller;


import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/prediction/players/predict")
public class TensorflowPredictController {

    @RequestMapping(
            value = "receipt",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_AUTOMATION')")
    public void predictReceipt(
            @RequestParam("id") UUID id,
            @RequestBody JsonNode results) {

        //   tensorflowPredictService.receiveResult(id, new JSONObject(results.toString()));
    }

}
