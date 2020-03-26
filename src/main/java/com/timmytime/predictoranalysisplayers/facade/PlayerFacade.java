package com.timmytime.predictoranalysisplayers.facade;

import com.timmytime.predictoranalysisplayers.response.data.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class PlayerFacade {

    @Value("${data.host}")
    private String dataHost;

    @Value("${player.url}")
    private String playerUrl;

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private AuthApiFacade authApiFacade;

    public List<Player> getPlayersByCompetition(String competition) {
        return Arrays.asList();
    }
}
