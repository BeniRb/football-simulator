package com.football.server.database;

import com.football.server.entities.Team;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TeamRepository {

    private final SessionFactory sessionFactory;

    // Spring will automatically inject your configured Hibernate SessionFactory here
    public TeamRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Team> findAllTeams() {
        List<Team> teams = new ArrayList<>();
        // Open a session to interact with your mapped entities
        try (Session session = sessionFactory.openSession()) {
            // HQL query using your mapped Team entity name
            Query<Team> query = session.createQuery("from Team", Team.class);
            teams = query.list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return teams;
    }
}