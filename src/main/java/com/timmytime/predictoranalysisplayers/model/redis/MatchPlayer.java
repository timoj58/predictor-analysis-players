package com.timmytime.predictoranalysisplayers.model.redis;


import com.timmytime.predictoranalysisplayers.response.data.StatMetric;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MatchPlayer implements Serializable {


    private String name;
    private Integer duration;
    private List<Event> events = new ArrayList<>();

    public MatchPlayer() {

    }

    public MatchPlayer(String name, Integer duration, List<StatMetric> metrics) {
        this.name = name;
        this.duration = duration;
        metrics.stream().forEach(
                metric -> events.add(new Event(
                        metric.getLabel(), metric.getTimeOfMetric() != null ?
                        String.valueOf(metric.getTimeOfMetric() / 60).replace(".", ":")
                        : "Unknown")));

    }


}
