package com.timmytime.predictoranalysisplayers.transformer;

import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.facade.StatMetricFacade;
import com.timmytime.predictoranalysisplayers.model.redis.Event;
import com.timmytime.predictoranalysisplayers.model.redis.PlayerAppearance;
import com.timmytime.predictoranalysisplayers.response.data.MatchResponse;
import com.timmytime.predictoranalysisplayers.response.data.StatMetric;
import com.timmytime.predictoranalysisplayers.response.data.TeamStats;
import com.timmytime.predictoranalysisplayers.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;


@Component
public class PlayerAppearanceTransformer {

    private static final Logger log = LoggerFactory.getLogger(PlayerAppearanceTransformer.class);

    private final DateUtils dateUtils = new DateUtils();

    @Autowired
    private StatMetricFacade statMetricFacade;

    //going to need to grab stats at some point. for now...wait.

    public BiFunction<UUID, MatchResponse, PlayerAppearance> transform = (playerId, match) -> {
        TeamStats teamStats;
        Integer goalsConceded = 0;

        Boolean home = Boolean.FALSE;
        UUID playerTeam;
        UUID opponent;

        if(match.getPlayerTeam().equals(match.getHomeId())){
            teamStats = match.getHomeStats();
            goalsConceded = match.getAwayStats().getScore();
            home = Boolean.TRUE;
            playerTeam = match.getHomeId();
            opponent = match.getAwayId();
        }else{
            teamStats = match.getAwayStats();
            goalsConceded = match.getHomeStats().getScore();
            playerTeam = match.getAwayId();
            opponent = match.getHomeId();
        }

        PlayerAppearance playerAppearance = new PlayerAppearance(match, dateUtils.convert.apply(match.getDate()), home, playerTeam, opponent);
       //more to fix..for stats etc...tomorrow task.

        //not working out pro-rated data (goals conceded / saves) too messy (given red cards minutes wrong too).
        playerAppearance.getStatMetrics().add(new Event(FantasyEventTypes.GOALS_CONCEDED, goalsConceded));
        playerAppearance.getStatMetrics().add(new Event(FantasyEventTypes.MINUTES, match.getDuration()));

        List<StatMetric> player = statMetricFacade.findByIds(teamStats.getPlayerStatMetrics());
        List<StatMetric> team = statMetricFacade.findByIds(teamStats.getTeamStatMetrics());

        player.stream().filter(f -> f.getPlayer().equals(playerId)).forEach(
                stat ->  playerAppearance.getStatMetrics().add(new Event(stat)));

        if(match.getGoalkeeper()) {
            team.stream().filter(f -> f.getLabel().equals("saves")).forEach(
                    stat -> playerAppearance.getStatMetrics().add(new Event(stat))
            );
        }

        return playerAppearance;
    };
}
