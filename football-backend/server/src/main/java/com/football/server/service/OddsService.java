package com.football.server.service;

import com.football.server.entities.GameMatch;
import com.football.server.entities.Team;
import com.football.server.responses.OddsResponse;
import com.football.server.utils.Errors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OddsService {

    private final Persist persist;

    public OddsService(Persist persist) {
        this.persist = persist;
    }

    /**
     * Calculates dynamic odds for a match based on team skill metrics 
     * and saves them directly to the match record.
     */
    @Transactional
    public OddsResponse<Void> calculateAndSaveOdds(GameMatch match) {
        if (match == null) {
            return new OddsResponse<>(Errors.ERROR_NULL_MATCH_REFERENCE, false, "Match object cannot be null.");
        }

        // 1. Load the competing team entities to inspect skill levels
        Team home = persist.loadObject(Team.class, match.getHomeTeamId());
        Team away = persist.loadObject(Team.class, match.getAwayTeamId());

        if (home == null || away == null) {
            return new OddsResponse<>(Errors.ERROR_TEAM_NOT_FOUND, false, "One or both teams could not be found for the match.");
        }

        // 2. Determine base win probabilities using skill difference
        double skillDiff = home.getSkillLevel() - away.getSkillLevel();
        
        // Base probabilities for completely equal teams
        double baseHome = 0.40 + 0.05; // 40% base + 5% home-field advantage
        double baseAway = 0.30;
        double baseDraw = 0.25;

        // Shift win chances based on skill gap
        double homeProb = baseHome + (skillDiff * 0.007);
        double awayProb = baseAway - (skillDiff * 0.007);
        
        // Draw probability shrinks as the skill gap widens in either direction
        double drawProb = baseDraw - (Math.abs(skillDiff) * 0.002);

        // Enforce logical probability floor limits (no team has a 0% or 100% chance)
        homeProb = Math.max(0.10, Math.min(0.80, homeProb));
        awayProb = Math.max(0.10, Math.min(0.80, awayProb));
        drawProb = Math.max(0.10, Math.min(0.50, drawProb));

        // 3. Inject a standard bookmaker margin (overround) of ~10% to ensure realistic odds profiles
        double bookmakerMargin = 1.10;
        
        // Odds = Bookmaker Margin / Probability
        double homeOdds = Math.round((bookmakerMargin / homeProb) * 100.0) / 100.0;
        double drawOdds = Math.round((bookmakerMargin / drawProb) * 100.0) / 100.0;
        double awayOdds = Math.round((bookmakerMargin / awayProb) * 100.0) / 100.0;

        // 4. Update and persist the match object
        match.setHomeOdds(homeOdds);
        match.setDrawOdds(drawOdds);
        match.setAwayOdds(awayOdds);
        
        persist.save(match);
        System.out.println(String.format("Odds Generated for Match %d [%s vs %s] -> Home: %.2f | Draw: %.2f | Away: %.2f", 
                match.getId(), home.getName(), away.getName(), homeOdds, drawOdds, awayOdds));

        return new OddsResponse<>(null, true, "Odds generated and saved successfully.");
    }
}