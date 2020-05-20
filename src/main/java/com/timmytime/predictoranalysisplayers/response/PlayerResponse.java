package com.timmytime.predictoranalysisplayers.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PlayerResponse {

    private String label;
    private UUID id;
    private String currentTeam;
    private Integer appearances;
    private Integer goals;
    private Integer assists;
    private Integer redCards;
    private Integer yellowCards;

    private List<FantasyResponse> fantasyResponse = new ArrayList<>();

}
