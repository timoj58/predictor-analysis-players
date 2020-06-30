package com.timmytime.predictoranalysisplayers.response;

import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MatchSelectionResponse {

    private Integer order;
    private String event;
    private List<PlayerResponse> playerResponses = new ArrayList<>();

    public MatchSelectionResponse(FantasyEventTypes fantasyEventTypes, List<PlayerResponse> playerResponses){
        this.event = fantasyEventTypes.name().toLowerCase();
        this.order = fantasyEventTypes.getOrder();
        this.playerResponses = playerResponses;
    }

}
