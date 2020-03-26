package com.timmytime.predictoranalysisplayers.response.data;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Match {

    private UUID id;
    private LocalDateTime date;
    private List<UUID> teams = new ArrayList<>();
    private Stats stats;

}
