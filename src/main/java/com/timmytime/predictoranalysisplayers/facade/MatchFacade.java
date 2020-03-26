package com.timmytime.predictoranalysisplayers.facade;

import com.timmytime.predictoranalysisplayers.response.data.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Component
public class MatchFacade {

    @Value("${data.host}")
    private String dataHost;

    @Value("${match.url}")
    private String matchUrl;

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private AuthApiFacade authApiFacade;


    public List<Match> findByTeamsContains(UUID id) {


        ParameterizedTypeReference<List<Match>> typeRef = new ParameterizedTypeReference<List<Match>>() {
        };

        return restTemplate.exchange(
                dataHost + matchUrl.replace("{team-id}", id.toString()),
                HttpMethod.GET,
                new HttpEntity<>(null, authApiFacade.authenticate()),
                typeRef)
                .getBody();

    }
}
