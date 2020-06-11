package com.timmytime.predictoranalysisplayers.model.redisson;

import com.timmytime.predictoranalysisplayers.response.data.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CompetitionTeamsResponse extends RedissonResponse {
    private String competition;
    private List<Team> teams = new ArrayList<>();

    public CompetitionTeamsResponse(String competition, List<Team> teams){
        this.competition = competition;
        this.teams = teams;
    }
}
