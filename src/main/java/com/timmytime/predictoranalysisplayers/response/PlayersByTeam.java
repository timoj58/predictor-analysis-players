package com.timmytime.predictoranalysisplayers.response;


import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PlayersByTeam {

    private UUID team;
    private List<UUID> players;
}
