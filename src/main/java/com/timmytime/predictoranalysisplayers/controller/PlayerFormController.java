package com.timmytime.predictoranalysisplayers.controller;


import com.timmytime.predictoranalysisplayers.model.redis.PlayerForm;
import com.timmytime.predictoranalysisplayers.response.PlayersByTeam;
import com.timmytime.predictoranalysisplayers.service.CompetitionService;
import com.timmytime.predictoranalysisplayers.service.PlayerFormService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Api(value = "Player Form", description = "API to player form")
@RequestMapping("/api/prediction/players/form")
public class PlayerFormController {

    @Autowired
    private PlayerFormService playerFormService;
    @Autowired
    private CompetitionService competitionService;


    @RequestMapping(
            value = "team/{team}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_AUTOMATION')")
    public ResponseEntity<PlayersByTeam> getAllPlayers(
            @PathVariable("team") UUID team

    ){
        return ResponseEntity.ok(playerFormService.getPlayers(team));
    }


    @RequestMapping(
            value = "player/{player-id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_AUTOMATION')")
    public ResponseEntity<PlayerForm> getPlayerForm(
            @PathVariable("player-id")UUID playerId){
        return ResponseEntity.ok(playerFormService.get(playerId));
    }

    @RequestMapping(
            value = "",
            method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_AUTOMATION')")
    public void load(
            @RequestParam(required = false, value = "competition") String competition
        ){

        if(competition == null){
            competitionService.load();;
        }else{
            competitionService.load(competition);
        }

        return;
    }


    @RequestMapping(
            value = "",
            method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_AUTOMATION')")
    public void clear(){
        playerFormService.clear();
        return;
    }


}
