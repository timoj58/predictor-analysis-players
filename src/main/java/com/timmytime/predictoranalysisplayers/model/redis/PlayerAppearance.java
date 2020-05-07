package com.timmytime.predictoranalysisplayers.model.redis;


import com.timmytime.predictoranalysisplayers.response.data.MatchResponse;
import com.timmytime.predictoranalysisplayers.response.data.StatMetric;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PlayerAppearance implements Serializable {

    private UUID matchId;
    private Date date;
    private Boolean home;
    private UUID playerTeam;
    private UUID opponent;
    private String homeTeam;
    private String awayTeam;


    private List<Event> statMetrics = new ArrayList<>();
    private Integer duration;

    public PlayerAppearance(){

    }

    public PlayerAppearance(MatchResponse matchResponse, Date date, Boolean home, UUID playerTeam, UUID opponent){
        this.matchId = matchResponse.getId();
        this.date = date;
        this.home = home;
        this.playerTeam = playerTeam;
        this.opponent = opponent;
        this.homeTeam = matchResponse.getHome();
        this.awayTeam = matchResponse.getAway();
        this.duration = matchResponse.getDuration();
    }

}

