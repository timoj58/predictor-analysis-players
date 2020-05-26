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
public class PlayerFacade {

    @Value("${data.host}")
    private String dataHost;

    @Value("${players.url}")
    private String playersUrl;

    @Value("${player.url}")
    private String playerUrl;

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

    public Optional<Player> findById(UUID id){
        return
                Optional.ofNullable(
                        restTemplate.exchange(
                                dataHost + playerUrl.replace("{id}", id.toString()),
                                HttpMethod.GET,
                                new HttpEntity<>(null, authApiFacade.authenticate()),
                                Player.class)
                                .getBody()
                );
    }
}
