package com.timmytime.predictoranalysisplayers.model.redis;

import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.response.data.StatMetric;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class Event implements Serializable {

    private FantasyEventTypes eventType;
    private String label;
    private Integer value = 0;

    public Event(FantasyEventTypes eventType, Integer value){
        this.eventType = eventType;
        this.label = eventType.name().toLowerCase();
        this.value = value;
    }


    /*
    "Goal",
    "Goal - Free-kick",
    "Goal - Header",
    "Golden Goal",
    "Own Goal",
    "Penalty - Scored",
    "Red Card",
    "Yellow Card",
    "foulsCommited",
    "foulsSuffered",
    "goalAssists",
    "goals",
    "onTarget",
    "possession",
    "saves",
    "shots",
    "shotsOnTarget",
    "totalShots",
    "wonCorners"
]
     */

    public Event(StatMetric statMetric){

        switch (statMetric.getLabel()){
            case "Red Card":
                this.eventType = FantasyEventTypes.RED_CARD;
                break;
            case "Yellow Card":
                this.eventType = FantasyEventTypes.YELLOW_CARD;
                break;
            case "saves":
                this.eventType = FantasyEventTypes.SAVES;
                break;
            case "goals":    //need to fix this.  double counting i think. due to old stats still present.
                this.eventType = FantasyEventTypes.GOALS;
                break;
            case "Goal":
            case "Goal - Free-kick":
            case "Goal - Header":
            case "Penalty - Scored":
                this.eventType = FantasyEventTypes.GOAL_TYPE;
                break;
            case "Own Goal":
                this.eventType = FantasyEventTypes.OWN_GOALS;
                break;
            case "totalShots":
                this.eventType = FantasyEventTypes.SHOTS;
                break;
            case "shotsOnTarget":
                this.eventType = FantasyEventTypes.ON_TARGET;
                break;
            case "foulsCommited":
                this.eventType = FantasyEventTypes.FOULS_COMMITED;
                break;
            case "foulsSuffered":
                this.eventType = FantasyEventTypes.FOULS_RECEIVED;
                break;
            case "goalAssists":
                this.eventType = FantasyEventTypes.ASSISTS;
                break;
            default:
                this.eventType = FantasyEventTypes.UNKNOWN;
                break;
        }

        this.label = statMetric.getLabel();
        this.value = statMetric.getValue();
    }


}
