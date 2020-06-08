package com.timmytime.predictoranalysisplayers.response;

import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.model.redis.PlayerForm;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TopPerformerResponse {

    private String label;
    private UUID id;
    private FantasyEventTypes fantasyEventTypes;
    private FantasyResponse fantasyResponse;

    public TopPerformerResponse(PlayerForm playerForm, FantasyEventTypes fantasyEventTypes){
        this.id = playerForm.getId();
        this.label = playerForm.getLabel();
        this.fantasyEventTypes = fantasyEventTypes;
    }
}
