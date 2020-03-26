package com.timmytime.predictoranalysisplayers.response.data;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class StatMetric {

    private UUID id;
    private LocalDateTime timestamp;
    private String label;
    private UUID player;
    private Integer value = 1;
    private Integer timeOfMetric;

}
