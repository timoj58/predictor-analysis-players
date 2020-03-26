package com.timmytime.predictoranalysisplayers.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictoranalysisplayers.response.data.Player;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@ReadingConverter
public class ByteToPlayerConverter implements Converter<byte[], Player> {

    private Jackson2JsonRedisSerializer<Player> serializer;

    public ByteToPlayerConverter() {
        serializer = new Jackson2JsonRedisSerializer<>(Player.class);
        serializer.setObjectMapper(new ObjectMapper());

    }

    @Override
    public Player convert(byte[] bytes) {
        return serializer.deserialize(bytes);
    }
}
