package com.timmytime.predictoranalysisplayers.controller;

import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.model.redis.PlayerForm;
import com.timmytime.predictoranalysisplayers.response.MatchPrediction;
import com.timmytime.predictoranalysisplayers.response.TopPerformerResponse;
import com.timmytime.predictoranalysisplayers.service.CompetitionService;
import com.timmytime.predictoranalysisplayers.service.MatchService;
import com.timmytime.predictoranalysisplayers.service.PlayerResponseService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Api(value = "Competition", description = "API ...")
@RequestMapping("/api/prediction/players/competition")
public class CompetitionController {

    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private MatchService matchService;
    @Autowired
    private PlayerResponseService playerResponseService;

    @RequestMapping(
            value = "match",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_AUTOMATION')")
    public ResponseEntity<MatchPrediction> getMatchPrediction(
            @RequestParam("home") UUID home,
            @RequestParam("away") UUID away){
       return ResponseEntity.ok(matchService.get(home, away));
    }


    @RequestMapping(
            value = "{competition}/top-performers",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_AUTOMATION')")
    public ResponseEntity<List<TopPerformerResponse>> getTopPerformers(
            @PathVariable("competition") String competition,
            @RequestParam("type") String type){
        return ResponseEntity.ok(playerResponseService.topPerformers(competition, FantasyEventTypes.valueOf(type)));
    }


    @RequestMapping(
            value = "{competition}/top-picks",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_AUTOMATION')")
    public ResponseEntity<List<TopPerformerResponse>> getTopPicks(
            @PathVariable("competition") String competition,
            @RequestParam("type") String type){
        return ResponseEntity.ok(playerResponseService.topPicks(competition, FantasyEventTypes.valueOf(type)));
    }


}
