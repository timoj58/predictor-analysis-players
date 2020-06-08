package com.timmytime.predictoranalysisplayers.model.redisson;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MatchSelectionResponse {

    private String event;
    private List<PlayerResponse> playerResponses = new ArrayList<>();

    public MatchSelectionResponse(String event, List<PlayerResponse> playerResponses){
        this.event = event;
        this.playerResponses = playerResponses;
    }

}
