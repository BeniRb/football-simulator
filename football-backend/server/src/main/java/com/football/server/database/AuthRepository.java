package com.football.server.database;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.football.server.entities.User;
import com.football.server.service.Persist;
import java.util.List;

@Repository
public class AuthRepository {
    private final Persist persist;

    public AuthRepository(Persist persist) {
        this.persist = persist;
    }

    @Transactional
    public User findUserById(long id) {
        return persist.getQuerySession()
                .createQuery("FROM com.football.server.entities.User WHERE id = :id", User.class)
                .setParameter("id", id)
                .uniqueResult();
    }

    @Transactional
    public User findUserByUsername(String username) {
        if (username == null) {
            return null;
        }
        return persist.getQuerySession()
                .createQuery("FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .uniqueResult();
    }

    @Transactional
public User getUserByUsernameAndPassword(String username, String password) {
    return persist.getQuerySession()
            .createQuery("FROM com.football.server.entities.User WHERE username = :username AND password = :password", User.class)
            .setParameter("username", username)
            .setParameter("password", password)
            .uniqueResult();
}
    @Transactional
    public void saveUser(User user) {
        if (user != null) {
            persist.save(user);
        }
    }
    @Transactional
    public List<com.football.server.entities.User> findAllByOrderByBalanceDesc() {
        return persist.getQuerySession()
                .createQuery("FROM User u ORDER BY u.balance DESC", com.football.server.entities.User.class)
                .list();
    }
}