package com.timmytime.predictoranalysisplayers.model.mongo;

import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class FantasyOutcome {

    private Boolean success;
    private UUID playerId;
    private FantasyEventTypes fantasyEventType;
}
