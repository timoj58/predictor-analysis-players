package com.timmytime.predictoranalysisplayers.model.redisson;

import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.response.PlayerResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class TopSelectionsResponse extends RedissonResponse {

    private String event;
    private List<PlayerResponse> playerResponses = new ArrayList<>();

    public TopSelectionsResponse(String event, List<PlayerResponse> playerResponses){
        this.event = event;
        this.playerResponses = playerResponses;
    }

    public void process(List<PlayerResponse> toProcess){

        this.playerResponses.addAll(toProcess);
        this.playerResponses =
                playerResponses.stream().sorted(Comparator.comparing(PlayerResponse::getFantasyEventScore).reversed())
                .limit(event.equals(FantasyEventTypes.SAVES.name().toLowerCase()) ? 10 : 20).collect(Collectors.toList());

    }

}
