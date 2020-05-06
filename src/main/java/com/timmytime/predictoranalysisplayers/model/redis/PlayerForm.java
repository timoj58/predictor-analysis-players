package com.timmytime.predictoranalysisplayers.model.redis;

import com.timmytime.predictoranalysisplayers.response.data.Player;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@RedisHash("PlayerForm")
public class PlayerForm {

    @Id
    private UUID id;
    private String label;
    private UUID team;
    private List<PlayerAppearance> playerAppearances = new ArrayList<>();

    public PlayerForm(){

    }

    public PlayerForm(Player player){
        this.label = player.getLabel();
        this.team  = player.getLatestTeam();
        this.id = player.getId();
    }

}
