package com.timmytime.predictoranalysisplayers.facade;

import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.response.PlayerEventOutcomeCsv;
import com.timmytime.predictoranalysisplayers.util.rest.RestTemplateHelper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TensorflowFacade extends RestTemplateHelper {

    private static final Logger log = LoggerFactory.getLogger(TensorflowFacade.class);

    @Value("${ml.host}")
    private String mlHost;

    @Value("${ml.train.goals.url}")
    private String trainGoalsUrl;

    @Value("${ml.train.assists.url}")
    private String trainAssistsUrl;

    @Value("${ml.train.minutes.url}")
    private String trainMinutesUrl;

    @Value("${ml.train.conceded.url}")
    private String trainConcededUrl;

    @Value("${ml.train.saves.url}")
    private String trainSavesUrl;

    @Value("${ml.predict.goals.url}")
    private String predictGoalsUrl;

    @Value("${ml.predict.assists.url}")
    private String predictAssistsUrl;

    @Value("${ml.predict.minutes.url}")
    private String predictMinutesUrl;

    @Value("${ml.predict.conceded.url}")
    private String predictConcededUrl;

    @Value("${ml.predict.saves.url}")
    private String predictSavesUrl;

    /*
      so two train an predict...simply enough....
     */

    public JSONObject train(FantasyEventTypes fantasyEventTypes, UUID receipt){

        String url = mlHost + getUrl(fantasyEventTypes, Boolean.TRUE)
                .replace("<player>", UUID.randomUUID().toString())
                .replace("<receipt>", receipt.toString());

        log.info("url is {}", url);

        return new JSONObject(
                restTemplate.postForEntity(
                        url,
                        null,
                        String.class)
                        .getBody());
    }

    public JSONObject predict(UUID player, FantasyEventTypes fantasyEventTypes, PlayerEventOutcomeCsv playerEventOutcomeCsv, UUID receipt){
        return new JSONObject(
                restTemplate.postForEntity(
                        mlHost + getUrl(fantasyEventTypes, Boolean.FALSE)
                                .replace("<receipt>", receipt.toString())
                                .replace("<player>", player.toString()),
                        playerEventOutcomeCsv.getJson(),
                        String.class)
                        .getBody());
    }

    private String getUrl(FantasyEventTypes fantasyEventTypes, Boolean trainingMode) {
        switch (fantasyEventTypes) {
            case GOALS:
                return trainingMode? trainGoalsUrl : predictGoalsUrl;
            case ASSISTS:
                return trainingMode? trainAssistsUrl : predictAssistsUrl;
            case SAVES:
                return trainingMode? trainSavesUrl : predictSavesUrl;
            case MINUTES:
                return trainingMode? trainMinutesUrl : predictMinutesUrl;
            case GOALS_CONCEDED:
                return trainingMode? trainConcededUrl : predictConcededUrl;
        }

        return "";
    }

}
