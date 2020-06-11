package com.timmytime.predictoranalysisplayers.response;


import com.timmytime.predictoranalysisplayers.response.data.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PlayersByTeam {

    private UUID team;
    private List<Player> players;
}
