package com.timmytime.predictoranalysisplayers.facade;

import com.timmytime.predictoranalysisplayers.response.data.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class PlayerFacade {

    @Value("${data.host}")
    private String dataHost;

    @Value("${players.url}")
    private String playersUrl;

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private AuthApiFacade authApiFacade;

    public List<Player> getPlayersByCompetition(String competition) {
        ParameterizedTypeReference<List<Player>> typeRef = new ParameterizedTypeReference<List<Player>>() {
        };

        return restTemplate.exchange(
                dataHost + playersUrl.replace("{competition}", competition),
                HttpMethod.GET,
                new HttpEntity<>(null, authApiFacade.authenticate()),
                typeRef)
                .getBody();

    }
}
