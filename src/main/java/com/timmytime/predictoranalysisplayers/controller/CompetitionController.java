package com.timmytime.predictoranalysisplayers.controller;

import com.timmytime.predictoranalysisplayers.model.redis.PlayerForm;
import com.timmytime.predictoranalysisplayers.response.MatchPrediction;
import com.timmytime.predictoranalysisplayers.service.CompetitionService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Api(value = "Competition", description = "API ...")
@RequestMapping("/api/prediction/players/competition")
public class CompetitionController {

    @Autowired
    private CompetitionService competitionService;

    @RequestMapping(
            value = "match",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_AUTOMATION')")
    public ResponseEntity<MatchPrediction> getMatchPrediction(
            @RequestParam("home") UUID home,
            @RequestParam("away") UUID away){
       return ResponseEntity.ok(competitionService.get(home, away));
    }

}
