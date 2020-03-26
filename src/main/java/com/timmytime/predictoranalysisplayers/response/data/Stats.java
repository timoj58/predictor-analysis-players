package com.timmytime.predictoranalysisplayers.response.data;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Stats {

    private UUID id;
    private List<UUID> teamStats = new ArrayList<>();
}
