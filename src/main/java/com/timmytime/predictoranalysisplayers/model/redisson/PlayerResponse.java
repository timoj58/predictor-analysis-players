package com.timmytime.predictoranalysisplayers.model.redisson;

import com.timmytime.predictoranalysisplayers.response.FantasyResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PlayerResponse implements Serializable {

    private String label;
    private UUID id;
    private String currentTeam;
    private Integer appearances;
    private Integer goals;
    private Integer assists;
    private Integer redCards;
    private Integer yellowCards;
    private Integer saves;

    //this is for recent form...
    private Double hardmanRed;
    private Double hardmanYellow;
    private Double marksman;
    private Double wizard;


    private List<FantasyResponse> fantasyResponse = new ArrayList<>();

}
