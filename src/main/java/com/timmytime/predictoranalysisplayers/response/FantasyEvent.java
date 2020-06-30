package com.timmytime.predictoranalysisplayers.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FantasyEvent {

    private Double fantasyEventScore; //saves time.  also should use this for the mobile
    private String fantasyEventKey; //saves time.  also should use this for the mobile

    public FantasyEvent(Double fantasyEventScore, String fantasyEventKey){
        this.fantasyEventScore = fantasyEventScore;
        this.fantasyEventKey = fantasyEventKey;
    }
}
