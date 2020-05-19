package com.timmytime.predictoranalysisplayers.model.redis;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@RedisHash("fantasyResponse")
public class FantasyResponse {

    private String label;
    @Id
    private UUID id;
    //
    private Double minutes;
    private Double conceded;
    private Double saves;
    Map<Integer, Double> goals = new HashMap<>();
    Map<Integer, Double> assists = new HashMap<>();


}
