package com.timmytime.predictoranalysisplayers.model.mongo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PlayersByYear {

    @Id
    private Integer year;
    private Set<UUID> players = new HashSet<>();

}
