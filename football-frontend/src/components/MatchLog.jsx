import React, { useEffect, useRef, useState } from 'react';
import api from '../api/client';
import { translations } from '../utils/Translations';
import styles from '../styles/MatchLog.module.css';

export default function MatchLog({ language }) {
    const [logItems, setLogItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showCompletedStories, setShowCompletedStories] = useState(false);

    const lastShownCompletedIds = useRef(new Set());
    const completedTimerRef = useRef(null);
    const initializedRef = useRef(false);
    const showCompletedStoriesRef = useRef(false);

    const activeLang = language === 'HE' ? 'HE' : 'EN';
    const t = translations[activeLang];
    const isRtl = activeLang === 'HE';

    useEffect(() => {
        const isMatchCompleted = (match) => {
            return (
                match.isCompleted === true ||
                match.completed === true ||
                match.status === 'COMPLETED'
            );
        };

        const compileActivityLog = async () => {
            try {
                const [standingsRes, fixturesRes, historyRes] = await Promise.all([
                    api.get('/league/standings'),
                    api.get('/league/fixtures'),
                    api.get('/league/history')
                ]);

                if (
                    !standingsRes.data?.success ||
                    !fixturesRes.data?.success ||
                    !historyRes.data?.success
                ) {
                    return;
                }

                const teams = standingsRes.data.data || [];
                const upcomingMatches = fixturesRes.data.data || [];
                const completedMatches = historyRes.data.data || [];

                const teamsMap = {};
                teams.forEach((team, index) => {
                    const teamKey = String(team.id || team._id);
                    teamsMap[teamKey] = {
                        name: team.name,
                        rank: index + 1
                    };
                });

                const temporaryLog = [];
                const localTranslations = translations[activeLang];
                const getTeamName = (rawName) => localTranslations.teams?.[rawName] || rawName;

                const completed = completedMatches.filter(isMatchCompleted);
                const upcoming = upcomingMatches.filter(match => !isMatchCompleted(match));

                const newestCompleted = completed
                    .slice()
                    .sort((a, b) => Number(b.id) - Number(a.id))
                    .slice(0, 4);

                const newestCompletedKey = newestCompleted
                    .map(match => String(match.id))
                    .join('-');

                if (newestCompleted.length > 0 && !initializedRef.current) {
                    lastShownCompletedIds.current.add(newestCompletedKey);
                    initializedRef.current = true;
                }

                if (
                    initializedRef.current &&
                    newestCompleted.length > 0 &&
                    !lastShownCompletedIds.current.has(newestCompletedKey)
                ) {
                    lastShownCompletedIds.current.add(newestCompletedKey);

                    showCompletedStoriesRef.current = true;
                    setShowCompletedStories(true);

                    if (completedTimerRef.current) {
                        clearTimeout(completedTimerRef.current);
                    }

                    completedTimerRef.current = setTimeout(() => {
                        showCompletedStoriesRef.current = false;
                        setShowCompletedStories(false);
                    }, 7000);
                }

                if (showCompletedStoriesRef.current && newestCompleted.length > 0) {
                    newestCompleted.forEach(match => {
                        const homeId = String(match.homeTeamId);
                        const awayId = String(match.awayTeamId);

                        const home = teamsMap[homeId] || { name: `Team ${match.homeTeamId}`, rank: '-' };
                        const away = teamsMap[awayId] || { name: `Team ${match.awayTeamId}`, rank: '-' };

                        const homeName = getTeamName(home.name);
                        const awayName = getTeamName(away.name);

                        if (match.homeScore > match.awayScore) {
                            const scoreText = `${match.homeScore} - ${match.awayScore}`;

                            const homeText = activeLang === 'HE'
                                ? `📈 ${homeName} ${localTranslations.advancedAfterWin}${home.rank} ${localTranslations.afterWinningVs} ${awayName} (${scoreText})`
                                : `📈 ${homeName} ${localTranslations.advancedAfterWin} ${home.rank} ${localTranslations.afterWinningVs} ${awayName} (${scoreText})`;

                            const awayText = activeLang === 'HE'
                                ? `📉 ${awayName} ${localTranslations.droppedAfterLoss}${away.rank} ${localTranslations.afterLosingTo} ${homeName} (${scoreText})`
                                : `📉 ${awayName} ${localTranslations.droppedAfterLoss} ${away.rank} ${localTranslations.afterLosingTo} ${homeName} (${scoreText})`;

                            temporaryLog.push({
                                id: `match-h-win-${match.id}`,
                                text: homeText,
                                badge: localTranslations.won,
                                badgeColor: '#2e7d32'
                            });

                            temporaryLog.push({
                                id: `match-a-loss-${match.id}`,
                                text: awayText,
                                badge: localTranslations.lost,
                                badgeColor: '#c62828'
                            });
                        } else if (match.awayScore > match.homeScore) {
                            const scoreText = `${match.awayScore} - ${match.homeScore}`;

                            const awayText = activeLang === 'HE'
                                ? `📈 ${awayName} ${localTranslations.advancedAfterWin}${away.rank} ${localTranslations.afterWinningVs} ${homeName} (${scoreText})`
                                : `📈 ${awayName} ${localTranslations.advancedAfterWin} ${away.rank} ${localTranslations.afterWinningVs} ${homeName} (${scoreText})`;

                            const homeText = activeLang === 'HE'
                                ? `📉 ${homeName} ${localTranslations.droppedAfterLoss}${home.rank} ${localTranslations.afterLosingTo} ${awayName} (${scoreText})`
                                : `📉 ${homeName} ${localTranslations.droppedAfterLoss} ${home.rank} ${localTranslations.afterLosingTo} ${awayName} (${scoreText})`;

                            temporaryLog.push({
                                id: `match-a-win-${match.id}`,
                                text: awayText,
                                badge: localTranslations.won,
                                badgeColor: '#2e7d32'
                            });

                            temporaryLog.push({
                                id: `match-h-loss-${match.id}`,
                                text: homeText,
                                badge: localTranslations.lost,
                                badgeColor: '#c62828'
                            });
                        } else {
                            const scoreText = `${match.homeScore} - ${match.awayScore}`;

                            const homeText = activeLang === 'HE'
                                ? `🤝 ${homeName} ${localTranslations.remainedAfterDraw}${home.rank} ${localTranslations.afterDrawingVs} ${awayName} (${scoreText})`
                                : `🤝 ${homeName} ${localTranslations.remainedAfterDraw} ${home.rank} ${localTranslations.afterDrawingVs} ${awayName} (${scoreText})`;

                            const awayText = activeLang === 'HE'
                                ? `🤝 ${awayName} ${localTranslations.remainedAfterDraw}${away.rank} ${localTranslations.afterDrawingVs} ${homeName} (${scoreText})`
                                : `🤝 ${awayName} ${localTranslations.remainedAfterDraw} ${away.rank} ${localTranslations.afterDrawingVs} ${homeName} (${scoreText})`;

                            temporaryLog.push({
                                id: `match-h-draw-${match.id}`,
                                text: homeText,
                                badge: localTranslations.matchDrawLog,
                                badgeColor: '#757575'
                            });

                            temporaryLog.push({
                                id: `match-a-draw-${match.id}`,
                                text: awayText,
                                badge: localTranslations.matchDrawLog,
                                badgeColor: '#757575'
                            });
                        }
                    });
                } else {
                    upcoming.forEach(match => {
                        const homeId = String(match.homeTeamId);
                        const awayId = String(match.awayTeamId);

                        const rawHomeName = teamsMap[homeId]?.name || `Team ${match.homeTeamId}`;
                        const rawAwayName = teamsMap[awayId]?.name || `Team ${match.awayTeamId}`;

                        const homeName = getTeamName(rawHomeName);
                        const awayName = getTeamName(rawAwayName);

                        temporaryLog.push({
                            id: `upcoming-${match.id}`,
                            text: `⏳ ${homeName} ${localTranslations.vs} ${awayName}`,
                            badge: localTranslations.upcomingMatchLog,
                            badgeColor: '#0288d1'
                        });
                    });
                }

                setLogItems(temporaryLog);
            } catch (err) {
                console.error("Error setting log items:", err);
            } finally {
                setLoading(false);
            }
        };

        compileActivityLog();
        const intervalId = setInterval(compileActivityLog, 3000);

        return () => {
            clearInterval(intervalId);

            if (completedTimerRef.current) {
                clearTimeout(completedTimerRef.current);
            }
        };
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