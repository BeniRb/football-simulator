package com.football.server.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.football.server.entities.Team;
import com.football.server.entities.GameMatch;
import com.football.server.entities.LeagueSettings;

import java.util.ArrayList;
import java.util.List;

@Component
public class LeagueInitializerService implements CommandLineRunner {

    private final Persist persist;

    public LeagueInitializerService(Persist persist) {
        this.persist = persist;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Long teamCount = persist.getQuerySession()
                .createQuery("SELECT count(t) FROM com.football.server.entities.Team t", Long.class)
                .uniqueResult();

        if (teamCount == 0) {
            seedLeague();
        }
    }

    private void seedLeague() {
        List<Team> teams = new ArrayList<>();
        teams.add(new Team(null, "Maccabi Haifa", 9, 0, 0, 0));
        teams.add(new Team(null, "Maccabi Tel Aviv", 9, 0, 0, 0));
        teams.add(new Team(null, "Hapoel Beer Sheva", 8, 0, 0, 0));
        teams.add(new Team(null, "Beitar Jerusalem", 7, 0, 0, 0));
        teams.add(new Team(null, "Hapoel Haifa", 6, 0, 0, 0));
        teams.add(new Team(null, "Maccabi Netanya", 6, 0, 0, 0));
        teams.add(new Team(null, "Bnei Sakhnin", 5, 0, 0, 0));
        teams.add(new Team(null, "Hapoel Jerusalem", 5, 0, 0, 0));

        for (Team team : teams) {
            persist.save(team);
        }

        LeagueSettings settings = new LeagueSettings();
        settings.setCurrentRound(1);
        settings.setBettingWindowOpen(true);
        persist.save(settings);

        int numTeams = teams.size();
        int totalRounds = numTeams - 1; 
        int matchesPerRound = numTeams / 2; 

        for (int round = 0; round < totalRounds; round++) {
            for (int match = 0; match < matchesPerRound; match++) {
                int homeIdx = (round + match) % (numTeams - 1);
                int awayIdx = (numTeams - 1 - match + round) % (numTeams - 1);

                if (match == 0) {
                    awayIdx = numTeams - 1;
                }

                Team homeTeam = teams.get(homeIdx);
                Team awayTeam = teams.get(awayIdx);

                GameMatch gameMatch = new GameMatch();
                gameMatch.setHomeTeamId(homeTeam.getId());
                gameMatch.setAwayTeamId(awayTeam.getId());
                gameMatch.setRoundNumber(round + 1);
                gameMatch.setHomeScore(0);
                gameMatch.setAwayScore(0);
                gameMatch.setIsCompleted(false);
                gameMatch.setWeatherCondition("Clear");
                gameMatch.setIsKeyInjuryOccurred(false);

                persist.save(gameMatch);
            }
        }
    }
}