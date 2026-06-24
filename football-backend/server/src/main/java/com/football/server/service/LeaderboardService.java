package com.football.server.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.football.server.entities.Team;
import com.football.server.entities.User;
import com.football.server.requests.LeaderboardDto;

import java.util.ArrayList;
import java.util.List;

@Service
public class LeaderboardService {

    private final Persist persist;

    public LeaderboardService(Persist persist) {
        this.persist = persist;
    }

    // Existing method for Team Standings (/league/standings)
    @Transactional(readOnly = true)
    public List<Team> getLeaderboard() {
        List<Team> teams = persist.getQuerySession()
                .createQuery("FROM com.football.server.entities.Team", Team.class)
                .list();

        teams.sort((t1, t2) -> {
            if (t2.getPoints() != t1.getPoints()) {
                return Integer.compare(t2.getPoints(), t1.getPoints());
            }
            int gd2 = t2.getGoalsScored() - t2.getGoalsConceded();
            int gd1 = t1.getGoalsScored() - t1.getGoalsConceded();
            if (gd2 != gd1) {
                return Integer.compare(gd2, gd1);
            }
            return Integer.compare(t2.getGoalsScored(), t1.getGoalsScored());
        });

        return teams;
    }

    // New method for Admin User Balance Ranking (/admin/leaderboard)
    @Transactional(readOnly = true)
    public List<LeaderboardDto> getUserBalanceLeaderboard() {
        List<User> sortedUsers = persist.getQuerySession()
                .createQuery("FROM User u ORDER BY u.balance DESC", User.class)
                .list();

        List<LeaderboardDto> userLeaderboard = new ArrayList<>();
        int rank = 1;
        for (User user : sortedUsers) {
            userLeaderboard.add(new LeaderboardDto(rank++, user.getUsername(), user.getBalance()));
        }

        return userLeaderboard;
    }
}