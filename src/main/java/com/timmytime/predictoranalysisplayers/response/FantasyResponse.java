package com.timmytime.predictoranalysisplayers.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class FantasyResponse {

    private String opponent;
    private Boolean isHome;
    private Double minutes;
    private Double conceded;
    private Double saves;
    Map<Integer, Double> goals = new HashMap<>();
    Map<Integer, Double> assists = new HashMap<>();
    Map<Integer, Double> redCards = new HashMap<>();
    Map<Integer, Double> yellowCards = new HashMap<>();


}
