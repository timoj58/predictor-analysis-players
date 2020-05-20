package com.timmytime.predictoranalysisplayers.response.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UpcomingCompetitionEventsResponse implements Serializable {

  //  private String country
    private String competition;
    private List<UpcomingEventResponse> upcomingEventResponses = new ArrayList<>();
}
