package com.timmytime.predictoranalysisplayers.model.redis;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Event implements Serializable {

    private String label;
    private String time;

    public Event() {

    }

    public Event(String label, String time) {
        this.label = label;
        this.time = time;
    }

}
