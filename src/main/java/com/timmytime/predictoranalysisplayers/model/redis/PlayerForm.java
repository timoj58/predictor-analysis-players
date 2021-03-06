package com.timmytime.predictoranalysisplayers.model.redis;

import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.response.data.Player;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.ZoneOffset;
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
    @Indexed
    private UUID team;
    private List<PlayerAppearance> playerAppearances = new ArrayList<>();
    private long lastAppearance;

    public PlayerForm(){

    }

    public PlayerForm(Player player){
        this.label = player.getLabel();
        this.team  = player.getLatestTeam();
        this.id = player.getId();
        this.lastAppearance = player.getLastAppearance().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    public Boolean isGoalKeeper(){
        return playerAppearances.stream()
                .anyMatch(f -> f.getStatMetrics().stream().anyMatch(s -> s.getEventType().equals(FantasyEventTypes.SAVES)));
    }

}
