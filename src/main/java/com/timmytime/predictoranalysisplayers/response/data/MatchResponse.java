package com.timmytime.predictoranalysisplayers.response.data;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class MatchResponse {

    private UUID id;
    private String home;
    private UUID homeId;
    private TeamStats homeStats;
    private String away;
    private UUID awayId;
    private TeamStats awayStats;
    private LocalDateTime date;
    private Integer duration;
    private UUID playerTeam;
    private Boolean goalkeeper = Boolean.FALSE;

}
