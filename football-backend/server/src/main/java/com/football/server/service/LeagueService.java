package com.football.server.service;

import com.football.server.entities.GameMatch;
import com.football.server.entities.LeagueSettings;
import com.football.server.entities.Team;
import com.football.server.responses.LeagueResponse;
import com.football.server.utils.Errors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LeagueService {

    private final Persist persist;

    public LeagueService(Persist persist) {
        this.persist = persist;
    }

    @Transactional(readOnly = true)
    public LeagueResponse<List<GameMatch>> getCurrentRoundFixtures() {
        LeagueSettings settings = (LeagueSettings) persist.getQuerySession()
                .createQuery("FROM com.football.server.entities.LeagueSettings")
                .uniqueResult();

        if (settings == null) {
            return new LeagueResponse<>(Errors.ERROR_LEAGUE_SETTINGS_NOT_FOUND, false, "League settings missing.");
        }

        // Always fetch the active current round matches immediately so live odds are instantly accessible
        int roundToFetch = settings.getCurrentRound();

        List<GameMatch> fixtures = persist.getQuerySession()
                .createQuery("FROM com.football.server.entities.GameMatch WHERE roundNumber = :round", GameMatch.class)
                .setParameter("round", roundToFetch)
                .list();

        return new LeagueResponse<>(null, true, "Fixtures retrieved successfully.", fixtures);
    }

    @Transactional(readOnly = true)
    public LeagueResponse<List<Team>> getStandings() {
        List<Team> standings = persist.getQuerySession()
                .createQuery("FROM com.football.server.entities.Team ORDER BY points DESC, (goalsScored - goalsConceded) DESC, name ASC", Team.class)
                .list();

        return new LeagueResponse<>(null, true, "League table standings retrieved successfully.", standings);
    }

    @Transactional(readOnly = true)
    public LeagueResponse<List<GameMatch>> getAllCompletedFixtures() {
        List<GameMatch> fixtures = persist.getQuerySession()
                .createQuery("FROM com.football.server.entities.GameMatch WHERE isCompleted = true", GameMatch.class)
                .list();
        return new LeagueResponse<>(null, true, "All history retrieved.", fixtures);
    }

    @Transactional(readOnly = true)
    public int getCurrentRound() {
        LeagueSettings settings = (LeagueSettings) persist.getQuerySession()
                .createQuery("FROM com.football.server.entities.LeagueSettings")
                .uniqueResult();

        if (settings == null) {
            throw new RuntimeException("League settings not found");
        }

        return settings.getCurrentRound();
    }
}