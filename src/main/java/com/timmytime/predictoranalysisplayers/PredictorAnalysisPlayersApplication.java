package com.timmytime.predictoranalysisplayers;

import com.timmytime.predictoranalysisplayers.converter.ByteToPlayerConverter;
import com.timmytime.predictoranalysisplayers.converter.ByteToUUIDConverter;
import com.timmytime.predictoranalysisplayers.converter.PlayerToByteConverter;
import com.timmytime.predictoranalysisplayers.converter.UUIDToByteConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.convert.RedisCustomConversions;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

@SpringBootApplication
@EnableSwagger2
public class PredictorAnalysisPlayersApplication {

    public static void main(String[] args) {
        SpringApplication.run(PredictorAnalysisPlayersApplication.class, args);
    }


    @Bean
    public RedisCustomConversions redisCustomConversions() {
        return new RedisCustomConversions(Arrays.asList(
                new PlayerToByteConverter(), new ByteToPlayerConverter(),
                new UUIDToByteConverter(), new ByteToUUIDConverter()));
    }

}
