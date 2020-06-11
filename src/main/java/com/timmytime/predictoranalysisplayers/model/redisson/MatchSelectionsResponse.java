package com.timmytime.predictoranalysisplayers.model.redisson;

import com.timmytime.predictoranalysisplayers.response.MatchSelectionResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class MatchSelectionsResponse extends RedissonResponse {
    private UUID home;
    private UUID away;
    private List<MatchSelectionResponse> matchSelectionResponses = new ArrayList<>();

    public MatchSelectionsResponse(
            UUID home,
            UUID away,
            List<MatchSelectionResponse> matchSelectionResponses
    ){
        this.home = home;
        this.away = away;
        this.matchSelectionResponses = matchSelectionResponses;
    }
}
