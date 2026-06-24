package com.football.server.database;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.football.server.entities.RefreshToken;
import com.football.server.service.Persist;

@Repository
public class RefreshTokenRepository {

    private final Persist persist;

    public RefreshTokenRepository(Persist persist) {
        this.persist = persist;
    }

    @Transactional
    public RefreshToken findByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        return persist.getQuerySession()
                .createQuery("FROM com.football.server.entities.RefreshToken WHERE userId = :userId",
                        RefreshToken.class)
                // Pass the Long variable directly
                .setParameter("userId", userId) 
                .uniqueResult();
    }

    @Transactional
    public RefreshToken findByToken(String token) {
        return persist.getQuerySession()
                .createQuery("FROM com.football.server.entities.RefreshToken WHERE token = :token",
                        RefreshToken.class)
                .setParameter("token", token)
                .uniqueResult();
    }

    @Transactional
    public void save(RefreshToken refreshToken) {
        persist.save(refreshToken);
    }

    @Transactional
    public void delete(RefreshToken refreshToken) {
        if (refreshToken != null) {
            persist.remove(refreshToken);
        }
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        if (userId == null) {
            return;
        }
        RefreshToken refreshToken = findByUserId(userId);
        if (refreshToken != null) {
            persist.remove(refreshToken);
        }
    }
}