import React, { useState, useEffect } from 'react';
import api from '../api/client';
import { translations } from '../utils/Translations';
import styles from '../styles/Dashboard.module.css';

export default function LiveDashboard({ language, onSelectBet, selectedBet }) {
    const [matches, setMatches] = useState([]);
    const [teamsMap, setTeamsMap] = useState({});
    const [loading, setLoading] = useState(true);
    const [windowOpen, setWindowOpen] = useState(true);

    const activeLang = language === 'HE' ? 'HE' : 'EN';
    const t = translations[activeLang] || translations.EN;
    const isRtl = activeLang === 'HE';

    useEffect(() => {
        const fetchDashboardData = async () => {
            try {
                const [standingsRes, fixturesRes, settingsRes] = await Promise.all([
                    api.get('/league/standings'),
                    api.get('/league/fixtures'),
                    api.get('/league/settings')
                ]);

                if (standingsRes.data?.success) {
                    const map = {};
                    standingsRes.data.data.forEach(team => {
                        map[team.id] = team.name;
                    });
                    setTeamsMap(map);
                }

                if (fixturesRes.data?.success) {
                    setMatches(fixturesRes.data.data || []);
                }

                if (settingsRes.data?.success && settingsRes.data?.data) {
                    setWindowOpen(settingsRes.data.data.bettingWindowOpen);
                }
            } catch (err) {
                console.error("Dashboard refresh error:", err);
            } finally {
                setLoading(false);
            }
        };

        fetchDashboardData();
        const intervalId = setInterval(fetchDashboardData, 1000);
        
        return () => clearInterval(intervalId);
    }, [activeLang]); 

    const handleSelect = (match, type, odds, homeName, awayName) => {
        if (!windowOpen) return;
        onSelectBet({
            matchId: match.id,
            predictionType: type,
            odds: odds,
            homeName: homeName,
            awayName: awayName
        });
    };

    if (loading && matches.length === 0) {
        return <div className={styles.loadingText}>{t.loadingLiveFeed || 'Loading...'}</div>;
    }

    return (
        <div className={styles.dashboardContainer} style={{ direction: isRtl ? 'rtl' : 'ltr' }}>
            <h2 className={styles.dashboardHeader}>{t.liveDashboard}</h2>
            
            {!windowOpen && (
                <div className={styles.closedBanner}>{t.bettingWindowClosed}</div>
            )}

            <div className={styles.oddsGrid}>
                {matches.length > 0 ? (
                    matches.map(match => {
                        const rawHome = teamsMap[match.homeTeamId] || `Team ${match.homeTeamId}`;
                        const rawAway = teamsMap[match.awayTeamId] || `Team ${match.awayTeamId}`;
                        
                        const homeName = t.teams?.[rawHome] || rawHome;
                        const awayName = t.teams?.[rawAway] || rawAway;

                        const isHomeActive = selectedBet?.matchId === match.id && selectedBet?.predictionType === 'home';
                        const isDrawActive = selectedBet?.matchId === match.id && selectedBet?.predictionType === 'draw';
                        const isAwayActive = selectedBet?.matchId === match.id && selectedBet?.predictionType === 'away';

                        return (
                            <div key={match.id} className={styles.oddsCard}>
                                <div className={styles.matchTeams}>
                                    {homeName} {t.vs} {awayName}
                                </div>
                                
                                <div className={styles.oddsContainer}>
                                    <button 
                                        disabled={!windowOpen}
                                        className={`${styles.oddBtn} ${styles.home || ''} ${isHomeActive ? styles.active : ''}`}
                                        onClick={() => handleSelect(match, 'home', match.homeOdds, homeName, awayName)}
                                        style={{ direction: 'ltr' }}
                                    >
                                        <span className={styles.btnLabel}>{t.homeWin}</span>
                                        <span className={styles.btnOdds}>{match.homeOdds}</span>
                                    </button>
                                    <button 
                                        disabled={!windowOpen}
                                        className={`${styles.oddBtn} ${styles.draw || ''} ${isDrawActive ? styles.active : ''}`}
                                        onClick={() => handleSelect(match, 'draw', match.drawOdds, homeName, awayName)}
                                        style={{ direction: 'ltr' }}
                                    >
                                        <span className={styles.btnLabel}>{t.draw}</span>
                                        <span className={styles.btnOdds}>{match.drawOdds}</span>
                                    </button>
                                    <button 
                                        disabled={!windowOpen}
                                        className={`${styles.oddBtn} ${styles.away || ''} ${isAwayActive ? styles.active : ''}`}
                                        onClick={() => handleSelect(match, 'away', match.awayOdds, homeName, awayName)}
                                        style={{ direction: 'ltr' }}
                                    >
                                        <span className={styles.btnLabel}>{t.awayWin}</span>
                                        <span className={styles.btnOdds}>{match.awayOdds}</span>
                                    </button>
                                </div>
                            </div>
                        );
                    })
                ) : (
                    <div className={styles.noMatchesMessage}>{t.noMatchesAvailable}</div>
                )}
            </div>
        </div>
    );
}