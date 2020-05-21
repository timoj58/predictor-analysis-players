package com.timmytime.predictoranalysisplayers.response.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class Player {

    private UUID id;
    private String label;
    private UUID latestTeam;
    private LocalDate lastAppearance;

    public Player(UUID id){
        this.id = id;
    }


}
