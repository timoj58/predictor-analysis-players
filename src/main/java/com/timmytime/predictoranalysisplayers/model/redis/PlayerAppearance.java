package com.timmytime.predictoranalysisplayers.model.redis;


import com.timmytime.predictoranalysisplayers.response.data.StatMetric;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PlayerAppearance implements Serializable {

    private UUID matchId;
    private List<StatMetric> statMetrics = new ArrayList<>();
    private Integer duration;

}

/*

public Map<String, List<Date>> getStats() {
    Map<String, List<StatMetric>> mapped =
            stats.stream().collect(groupingBy(StatMetric::getLabel));


    Map<String, List<Date>> formatted = new HashMap<>();
    mapped.keySet().stream().forEach(
            key -> formatted.put(key,
                    mapped.get(key)
                            .stream()
                            //need to review this at some point, with new LocalDateTime
                            .map(m -> new Date(
                                    m.getTimestamp().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                            +
                                            (m.getTimeOfMetric() != null ?
                                                    m.getTimeOfMetric() * 1000
                                                    : 0)))
                            .collect(Collectors.toList()))
    );

    return formatted;
}

 */
