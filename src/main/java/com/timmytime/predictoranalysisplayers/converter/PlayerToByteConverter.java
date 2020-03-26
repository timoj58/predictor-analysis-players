package com.timmytime.predictoranalysisplayers.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictoranalysisplayers.response.data.Player;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@WritingConverter
public class PlayerToByteConverter implements Converter<Player, byte[]> {
    private Jackson2JsonRedisSerializer<Player> serializer;

    public PlayerToByteConverter() {
        serializer = new Jackson2JsonRedisSerializer<>(Player.class);
        serializer.setObjectMapper(new ObjectMapper());
    }

    @Override
    public byte[] convert(Player player) {
        return serializer.serialize(player);
    }
}
