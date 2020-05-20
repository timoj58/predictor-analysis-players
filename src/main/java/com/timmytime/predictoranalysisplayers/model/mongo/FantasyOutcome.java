package com.timmytime.predictoranalysisplayers.model.mongo;

import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class FantasyOutcome {

    @Id
    private UUID id;
    private UUID playerId;
    private Boolean success;
    private FantasyEventTypes fantasyEventType;
    private String prediction;
    private String home;
    private UUID opponent;

    public FantasyOutcome(UUID id){
        this.id = id;
    }
}
