package com.timmytime.predictoranalysisplayers.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

public class StatMetricFacade {

    @Value("${data.host}")
    private String dataHost;

    @Value("${team.stats.url}")
    private String teamStatsUrl;

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private AuthApiFacade authApiFacade;

}
