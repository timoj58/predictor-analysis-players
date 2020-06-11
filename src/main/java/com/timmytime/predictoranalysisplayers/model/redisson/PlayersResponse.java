package com.timmytime.predictoranalysisplayers.model.redisson;

import com.timmytime.predictoranalysisplayers.response.PlayerResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PlayersResponse extends RedissonResponse {

    private List<PlayerResponse> playerResponses = new ArrayList<>();

    public PlayersResponse(List<PlayerResponse> playerResponses){
        this.playerResponses = playerResponses;
    }

}
