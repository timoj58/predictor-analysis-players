package com.timmytime.predictoranalysisplayers.model.redis;

import com.timmytime.predictoranalysisplayers.response.data.Player;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RedisHash("PlayerForm")
public class PlayerForm {
    @Indexed
    private Player player;
    private List<PlayerAppearance> playerAppearances = new ArrayList<>();

}
