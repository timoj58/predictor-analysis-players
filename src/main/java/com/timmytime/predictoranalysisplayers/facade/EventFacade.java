package com.timmytime.predictoranalysisplayers.facade;

import com.timmytime.predictoranalysisplayers.response.data.UpcomingCompetitionEventsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class EventFacade {

    @Value("${events.host}")
    private String eventHost;

    @Value("${events.url}")
    private String eventsUrl;

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private AuthApiFacade authApiFacade;


    public List<UpcomingCompetitionEventsResponse> upcomingEvents(String country, String competition){
        ParameterizedTypeReference<List<UpcomingCompetitionEventsResponse>> typeRef = new ParameterizedTypeReference<List<UpcomingCompetitionEventsResponse>>() {
        };

        return restTemplate.exchange(
                eventHost + eventsUrl+"?competition="+competition+"&country="+country,
                HttpMethod.GET,
                new HttpEntity<>(null, authApiFacade.authenticate()),
                typeRef)
                .getBody();
    }

}
