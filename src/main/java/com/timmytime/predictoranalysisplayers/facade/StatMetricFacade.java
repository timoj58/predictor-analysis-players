package com.timmytime.predictoranalysisplayers.facade;

import com.timmytime.predictoranalysisplayers.response.data.MatchResponse;
import com.timmytime.predictoranalysisplayers.response.data.StatMetric;
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
public class StatMetricFacade {

    @Value("${data.host}")
    private String dataHost;

    @Value("${stat.metric.url}")
    private String statMetricUrl;

    @Value("${stat.metrics.url}")
    private String statMetricsUrl;

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private AuthApiFacade authApiFacade;


    public Optional<StatMetric> findById(UUID id){
        return
                Optional.ofNullable(
                        restTemplate.exchange(
                                dataHost + statMetricUrl.replace("{id}", id.toString()),
                                HttpMethod.GET,
                                new HttpEntity<>(null, authApiFacade.authenticate()),
                                StatMetric.class)
                                .getBody()
                );
    }

    public List<StatMetric> findByIds(List<UUID> ids){
        ParameterizedTypeReference<List<StatMetric>> typeRef = new ParameterizedTypeReference<List<StatMetric>>() {
        };

        return restTemplate.exchange(
                dataHost + statMetricsUrl,
                HttpMethod.POST,
                new HttpEntity<>(ids, authApiFacade.authenticate()),
                typeRef)
                .getBody();

    }


}
