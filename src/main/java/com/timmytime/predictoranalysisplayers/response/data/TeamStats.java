package com.timmytime.predictoranalysisplayers.response.data;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class TeamStats {

    private UUID id;
    private UUID team;
    private Boolean homeTeam = Boolean.FALSE;
    private Integer score;
    private List<UUID> playerStatMetrics = new ArrayList<>();
    private List<UUID> teamStatMetrics = new ArrayList<>();
    private UUID lineup;


}
