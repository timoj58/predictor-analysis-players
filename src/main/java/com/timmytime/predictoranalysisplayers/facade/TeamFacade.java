package com.timmytime.predictoranalysisplayers.facade;

import com.timmytime.predictoranalysisplayers.response.data.Player;
import com.timmytime.predictoranalysisplayers.response.data.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TeamFacade {

    @Value("${data.host}")
    private String dataHost;

    @Value("${team.url}")
    private String teamUrl;

    @Value("${teams.url}")
    private String teamsUrl;

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private AuthApiFacade authApiFacade;

    public List<Team> getTeamsByCompetition(String competition) {
        ParameterizedTypeReference<List<Team>> typeRef = new ParameterizedTypeReference<List<Team>>() {
        };

        return restTemplate.exchange(
                dataHost + teamsUrl.replace("{competition}", competition),
                HttpMethod.GET,
                new HttpEntity<>(null, authApiFacade.authenticate()),
                typeRef)
                .getBody();

    }

    public Optional<Team> findById(UUID id){
        return
                Optional.ofNullable(
                        restTemplate.exchange(
                                dataHost + teamUrl.replace("{id}", id.toString()),
                                HttpMethod.GET,
                                new HttpEntity<>(null, authApiFacade.authenticate()),
                                Team.class)
                                .getBody()
                );
    }
}
