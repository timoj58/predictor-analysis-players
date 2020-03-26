package com.timmytime.predictoranalysisplayers.response.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
public class Team implements Serializable {

    private UUID id;
    private String label;
    private String country;
    private String competition;

}
