package com.timmytime.predictoranalysisplayers.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PlayerResponse implements Serializable {

    private String label;
    private UUID id;
    private String currentTeam;
    private Integer appearances;
    private Integer goals;
    private Integer assists;
    private Integer redCards;
    private Integer yellowCards;
    private Integer saves;

    //this is for recent form...
    private Double hardmanRed;
    private Double hardmanYellow;
    private Double marksman;
    private Double wizard;

    private Double fantasyEventScore; //saves time.  also should use this for the mobile
    private String fantasyEventKey; //saves time.  also should use this for the mobile

    private List<FantasyResponse> fantasyResponse = new ArrayList<>();

    public PlayerResponse(PlayerResponse playerResponse, FantasyEvent fantasyEvent){
        this.label = playerResponse.getLabel();
        this.id = playerResponse.getId();
        this.currentTeam = playerResponse.getCurrentTeam();
        this.appearances = playerResponse.getAppearances();
        this.goals = playerResponse.getGoals();
        this.assists = playerResponse.getAssists();
        this.redCards = playerResponse.getRedCards();
        this.yellowCards = playerResponse.getYellowCards();
        this.saves = playerResponse.getSaves();
        this.hardmanRed = playerResponse.getHardmanRed();
        this.hardmanYellow = playerResponse.getHardmanYellow();
        this.marksman = playerResponse.getMarksman();
        this.wizard = playerResponse.getWizard();

        this.fantasyEventScore = fantasyEvent.getFantasyEventScore();
        this.fantasyEventKey = fantasyEvent.getFantasyEventKey();


        this.fantasyResponse = playerResponse.getFantasyResponse();
    }

}
