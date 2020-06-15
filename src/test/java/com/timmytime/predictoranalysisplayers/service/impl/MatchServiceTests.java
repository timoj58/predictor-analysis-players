package com.timmytime.predictoranalysisplayers.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.facade.EventFacade;
import com.timmytime.predictoranalysisplayers.facade.PlayerFacade;
import com.timmytime.predictoranalysisplayers.facade.TeamFacade;
import com.timmytime.predictoranalysisplayers.model.redisson.MatchSelectionsResponse;
import com.timmytime.predictoranalysisplayers.model.redisson.TopSelectionsResponse;
import com.timmytime.predictoranalysisplayers.receipt.Receipt;
import com.timmytime.predictoranalysisplayers.receipt.ReceiptManager;
import com.timmytime.predictoranalysisplayers.receipt.ReceiptUtils;
import com.timmytime.predictoranalysisplayers.repo.redisson.CompetitionTeamsResponseRepo;
import com.timmytime.predictoranalysisplayers.repo.redisson.IRedissonRepo;
import com.timmytime.predictoranalysisplayers.response.FantasyResponse;
import com.timmytime.predictoranalysisplayers.response.PlayerResponse;
import com.timmytime.predictoranalysisplayers.response.PlayersByTeam;
import com.timmytime.predictoranalysisplayers.response.data.Player;
import com.timmytime.predictoranalysisplayers.response.data.Team;
import com.timmytime.predictoranalysisplayers.response.data.UpcomingCompetitionEventsResponse;
import com.timmytime.predictoranalysisplayers.response.data.UpcomingEventResponse;
import com.timmytime.predictoranalysisplayers.service.PlayerFormService;
import com.timmytime.predictoranalysisplayers.service.PlayerResponseService;
import com.timmytime.predictoranalysisplayers.util.LambdaUtils;
import lombok.Getter;
import lombok.Setter;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MatchServiceTests {

    TeamFacade teamFacade = mock(TeamFacade.class);
    PlayerFormService playerFormService = mock(PlayerFormService.class);
    PlayerResponseService playerResponseService = mock(PlayerResponseService.class);
    EventFacade eventFacade = mock(EventFacade.class);
    MatchSelectionsRepo matchSelectionsResponseRepo = new MatchSelectionsRepo();
    TopSelectionRepo topSelectionsResponseRepo = new TopSelectionRepo();
    ReceiptManager receiptManager = new ReceiptManager(new ReceiptUtils());

    private final MatchServiceImpl competitionService
            = new MatchServiceImpl(
                    teamFacade,
            eventFacade,
            playerFormService,
            playerResponseService,
            matchSelectionsResponseRepo,
            topSelectionsResponseRepo
    );

    @Before
    public void init(){
        UpcomingCompetitionEventsResponse upcomingCompetitionEventsResponse = new UpcomingCompetitionEventsResponse();

        Team team = new Team();
        team.setId(UUID.randomUUID());
        team.setLabel("test");

        UpcomingEventResponse upcomingEventResponse = new UpcomingEventResponse();
        upcomingEventResponse.setHome(team);
        upcomingEventResponse.setAway(team);

        upcomingCompetitionEventsResponse.setUpcomingEventResponses(Arrays.asList(upcomingEventResponse));

        when(eventFacade.upcomingEvents("portugal", "portugal_1"))
                .thenReturn(Arrays.asList(upcomingCompetitionEventsResponse));

        when(teamFacade.findById(any(UUID.class))).thenReturn(Optional.of(team));

        Player player = new Player();
        player.setId(UUID.randomUUID());
        player.setLastAppearance(LocalDate.now());

        PlayersByTeam playersByTeam = new PlayersByTeam();
        playersByTeam.setTeam(team.getId());
        playersByTeam.setPlayers(Arrays.asList(player));

        when(playerFormService.getPlayers(any(UUID.class)))
                .thenReturn(playersByTeam);

        PlayerResponse playerResponse = new PlayerResponse();

        Map<Integer, Double> goals = new HashMap<>();
        goals.put(1, 22.3);
        goals.put(2, 5.3);

        Map<Integer, Double> assists = new HashMap<>();
        assists.put(1, 12.3);
        assists.put(2, 7.3);

        Map<Integer, Double> yellows = new HashMap<>();
        yellows.put(1, 41.3);

        FantasyResponse fantasyResponse = new FantasyResponse();
        fantasyResponse.setSaves(2.0);
        fantasyResponse.setGoals(goals);
        fantasyResponse.setAssists(assists);
        fantasyResponse.setYellowCards(yellows);

        playerResponse.setFantasyResponse(Arrays.asList(fantasyResponse));

        when(playerResponseService.get(player.getId()))
                .thenReturn(playerResponse);

    }

    @Test
    public void loadMatchSelections(){
        competitionService.loadMatches();

        assertTrue(
                topSelectionsResponseRepo.getSaved()
                        .get("portugal_1")
                        .stream()
                .filter(f -> f.getEvent().equals(FantasyEventTypes.GOALS.name().toLowerCase()))
                .findFirst()
                .get()
                .getPlayerResponses().stream().findFirst()
                .get()
                .getFantasyEventScore() == 27.6
        );

        assertTrue(
                topSelectionsResponseRepo.getSaved()
                        .get("portugal_1")
                        .stream()
                        .filter(f -> f.getEvent().equals(FantasyEventTypes.YELLOW_CARD.name().toLowerCase()))
                        .findFirst()
                        .get()
                        .getPlayerResponses().stream().findFirst()
                        .get()
                        .getFantasyEventScore() == 41.3
        );


    }


    @Getter
    @Setter
    public class TopSelectionRepo implements IRedissonRepo<TopSelectionsResponse>{

        private Map<String, List<TopSelectionsResponse>> saved = new HashMap<>();

        @Override
        public void save(TopSelectionsResponse topSelectionsResponse) throws JsonProcessingException {

        }

        @Override
        public void save(String key, TopSelectionsResponse topSelectionsResponse) throws JsonProcessingException {

            if(saved.containsKey(key)){
                List<TopSelectionsResponse> current = saved.get(key);
                current.add(topSelectionsResponse);
                saved.put(key, current);
            }else{
                List<TopSelectionsResponse> list = new ArrayList<>();
                list.add(topSelectionsResponse);
                saved.put(key, list);
            }
        }

        @Override
        public void deleteAll() {

        }

        @Override
        public void deleteAll(String key) {

        }
    }

    @Getter
    @Setter
    public class MatchSelectionsRepo implements IRedissonRepo<MatchSelectionsResponse>{

        private List<MatchSelectionsResponse> saved = new ArrayList<>();


        @Override
        public void save(MatchSelectionsResponse matchSelectionsResponse) throws JsonProcessingException {
            saved.add(matchSelectionsResponse);
        }

        @Override
        public void save(String key, MatchSelectionsResponse matchSelectionsResponse) throws JsonProcessingException {
            saved.add(matchSelectionsResponse);
        }

        @Override
        public void deleteAll() {

        }

        @Override
        public void deleteAll(String key) {

        }
    }
}
