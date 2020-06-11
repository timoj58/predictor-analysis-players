package com.timmytime.predictoranalysisplayers.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MatchPrediction {

    private String homeLabel;
    private String awayLabel;

    private List<PlayerResponse> homePlayers = new ArrayList<>();
    private List<PlayerResponse> awayPlayers = new ArrayList<>();

}
