package com.timmytime.predictoranalysisplayers.response.data;

import com.timmytime.predictoranalysisplayers.model.redis.PlayerForm;
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

    public Player(PlayerForm playerForm){
        this.id = playerForm.getId();
        this.label = playerForm.getLabel();
        this.latestTeam = playerForm.getTeam();
    }


}
