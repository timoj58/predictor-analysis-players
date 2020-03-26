package com.timmytime.predictoranalysisplayers;

import com.timmytime.predictoranalysisplayers.converter.ByteToPlayerConverter;
import com.timmytime.predictoranalysisplayers.converter.PlayerToByteConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.convert.RedisCustomConversions;

import java.util.Arrays;

@SpringBootApplication
public class PredictorAnalysisPlayersApplication {

    public static void main(String[] args) {
        SpringApplication.run(PredictorAnalysisPlayersApplication.class, args);
    }


    @Bean
    public RedisCustomConversions redisCustomConversions() {
        return new RedisCustomConversions(Arrays.asList(
                new PlayerToByteConverter(), new ByteToPlayerConverter()));
    }

}
