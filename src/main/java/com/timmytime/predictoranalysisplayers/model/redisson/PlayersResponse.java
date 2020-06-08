package com.timmytime.predictoranalysisplayers.model.redisson;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PlayersResponse implements Serializable {

    private List<PlayerResponse> playerResponses = new ArrayList<>();

    public PlayersResponse(List<PlayerResponse> playerResponses){
        this.playerResponses = playerResponses;
    }

}
