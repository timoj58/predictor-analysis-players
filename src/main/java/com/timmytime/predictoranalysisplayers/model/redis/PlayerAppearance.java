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
    private String home;
    private String away;
    private List<Event> statMetrics = new ArrayList<>();
    private Integer duration;

    public PlayerAppearance(){

    }

    public PlayerAppearance(MatchResponse matchResponse, Date date){
        this.matchId = matchResponse.getId();
        this.date = date;
        this.home = matchResponse.getHome();
        this.away = matchResponse.getAway();
        this.duration = matchResponse.getDuration();
    }

}

