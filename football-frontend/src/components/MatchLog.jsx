import React, { useEffect, useState } from 'react';
import api from '../api/client';
import { translations } from '../utils/Translations';
import styles from '../styles/MatchLog.module.css';

export default function MatchLog({ language }) {
    const [logItems, setLogItems] = useState([]);
    const [loading, setLoading] = useState(true);

    const activeLang = language === 'HE' ? 'HE' : 'EN';
    const t = translations[activeLang];
    const isRtl = activeLang === 'HE';

    useEffect(() => {
        const compileActivityLog = async () => {
            try {
                const [standingsRes, fixturesRes] = await Promise.all([
                    api.get('/league/standings'),
                    api.get('/league/fixtures')
                ]);

                if (!standingsRes.data?.success || !fixturesRes.data?.success) return;

                const teams = standingsRes.data.data || [];
                const matches = fixturesRes.data.data || [];

                const teamsMap = {};
                teams.forEach((team, index) => {
                    teamsMap[team.id] = {
                        name: team.name,
                        rank: index + 1
                    };
                });

                const temporaryLog = [];

                // Helper to safely fetch translated team names from translation object mapping
                const getTeamName = (rawName) => t.teams?.[rawName] || rawName;

                // 1. Process Completed Matches into Single-Row Stories
                const completed = matches.filter(m => m.isCompleted);
                completed.forEach(match => {
                    const home = teamsMap[match.homeTeamId] || { name: `Team ${match.homeTeamId}`, rank: '-' };
                    const away = teamsMap[match.awayTeamId] || { name: `Team ${match.awayTeamId}`, rank: '-' };

                    const homeName = getTeamName(home.name);
                    const awayName = getTeamName(away.name);

                    if (match.homeScore > match.awayScore) {
                        // Home Win Story (e.g., 2 - 1)
                        const scoreText = `${match.homeScore} - ${match.awayScore}`;
                        const homeText = activeLang === 'HE' 
                            ? `📈 ${homeName} ${t.advancedAfterWin}${home.rank} ${t.afterWinningVs} ${awayName} (${scoreText})`
                            : `📈 ${homeName} ${t.advancedAfterWin} ${home.rank} ${t.afterWinningVs} ${awayName} (${scoreText})`;
                        
                        temporaryLog.push({id: `match-h-${match.id}`,text: homeText,badge: t.matchFinishedLog,badgeColor: '#2e7d32'});
                    } else if (match.awayScore > match.homeScore) {
                        // Away Win Story
                        const scoreText = `${match.awayScore} - ${match.homeScore}`;
                        const awayText = activeLang === 'HE' 
                            ? `📈 ${awayName} ${t.advancedAfterWin}${away.rank} ${t.afterWinningVs} ${homeName} (${scoreText})`
                            : `📈 ${awayName} ${t.advancedAfterWin} ${away.rank} ${t.afterWinningVs} ${homeName} (${scoreText})`;

                        temporaryLog.push({id: `match-a-${match.id}`,text: awayText,badge: t.matchFinishedLog,badgeColor: '#2e7d32'});
                    } else {
                        // Draw Story
                        const scoreText = `${match.homeScore} - ${match.awayScore}`;
                        const drawText = activeLang === 'HE'
                            ? `🤝 ${homeName} ${t.remainedAfterDraw}${home.rank} ${t.afterDrawingVs} ${awayName} (${scoreText})`
                            : `🤝 ${homeName} ${t.remainedAfterDraw} ${home.rank} ${t.afterDrawingVs} ${awayName} (${scoreText})`;

                        temporaryLog.push({id: `match-d-${match.id}`,text: drawText,badge: t.matchDrawLog,badgeColor: '#757575'});
                    }
                });

                // 2. Process Upcoming Matches
                const upcoming = matches.filter(m => !m.isCompleted);
                upcoming.forEach(match => {
                    const rawHomeName = teamsMap[match.homeTeamId]?.name || `Team ${match.homeTeamId}`;
                    const rawAwayName = teamsMap[match.awayTeamId]?.name || `Team ${match.awayTeamId}`;

                    const homeName = getTeamName(rawHomeName);
                    const awayName = getTeamName(rawAwayName);

                    temporaryLog.push({
                        id: `upcoming-${match.id}`,
                        text: `⏳ ${homeName} ${t.vs} ${awayName}`,
                        badge: t.upcomingMatchLog,
                        badgeColor: '#0288d1'
                    });
                });

                setLogItems(temporaryLog);
            } catch (err) {
                console.error("Error setting log items:", err);
            } finally {
                setLoading(false);
            }
        };

        compileActivityLog();
        const intervalId = setInterval(compileActivityLog, 3000);

        return () => clearInterval(intervalId);
    }, [activeLang]);

    if (loading) return <div className={styles.loadingText}>{t.loadingActivity}</div>;

    return (
        <div 
            className={styles.logContainer} 
            style={{ direction: isRtl ? 'rtl' : 'ltr' }}
        >
            <h3 className={styles.logTitle}>
                {t.activityLogTitle}
            </h3>
            <div className={styles.scrollArea}>
                {logItems.length === 0 ? (
                    <p className={styles.emptyText}>{t.noActivity}</p>
                ) : (
                    logItems.map((item) => (
                        <div key={item.id} className={styles.logRow}>
                            <span className={styles.textItem}>{item.text}</span>
                            <span className={styles.statusBadge} style={{ backgroundColor: item.badgeColor }}>
                                {item.badge}
                            </span>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}