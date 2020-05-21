package com.timmytime.predictoranalysisplayers.model.redis;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@RedisHash("activeByYear")
public class ActivePlayersByYear {

    @Id
    private Integer year;
    private Set<UUID> players = new HashSet<>();

}
