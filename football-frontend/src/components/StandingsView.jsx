import React, { useEffect, useState, useRef } from 'react';
import api from '../api/client'; 
import { translations } from '../utils/Translations';
import styles from '../styles/StandingsView.module.css';

export default function StandingsView({ language, onDataLoaded }) {
    const [standings, setStandings] = useState([]);
    const [teamForms, setTeamForms] = useState({});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [currentRound, setCurrentRound] = useState(null);
    
    const [flashStatuses, setFlashStatuses] = useState({});
    const prevStandingsRef = useRef([]);
    const historyRef = useRef({});

    const activeLang = language || 'EN';
    const t = translations[activeLang] || translations.EN;
    const isRtl = activeLang === 'HE';

    useEffect(() => {
        const fetchTableAndFixturesData = async () => {
            try {
                // 1. Fetch data sequentially to avoid ReferenceErrors
                const standingsRes = await api.get('/league/standings');
                const fixturesRes = await api.get('/league/fixtures');
                const historyRes = await api.get('/league/history');

                if (!standingsRes.data?.success || !fixturesRes.data?.success || !historyRes.data?.success) {
                    setError(standingsRes.data?.message || t.error);
                    return;
                }

                // 2. Set current round safely after fixturesRes is defined
                const matches = fixturesRes.data.data || [];
                setCurrentRound(matches.length > 0 ? matches[0].roundNumber : null);

                const newStandings = standingsRes.data.data || [];
                const completedMatches = historyRes.data.data || [];

                if (newStandings.length === 0) historyRef.current = {};

                const tempMap = {};
                newStandings.forEach(team => { tempMap[team.name] = []; });
                
                const sorted = completedMatches.sort((a, b) => new Date(a.date) - new Date(b.date));
                
                sorted.forEach(match => {
                    const home = newStandings.find(t => t.id === match.homeTeamId);
                    const away = newStandings.find(t => t.id === match.awayTeamId);
                    
                    if (home && away) {
                        const resHome = match.homeScore > match.awayScore ? 'W' : (match.awayScore > match.homeScore ? 'L' : 'D');
                        const resAway = match.homeScore > match.awayScore ? 'L' : (match.awayScore > match.homeScore ? 'W' : 'D');
                        
                        tempMap[home.name].push(resHome);
                        tempMap[away.name].push(resAway);
                    }
                });

                historyRef.current = tempMap;
                setTeamForms({ ...historyRef.current });

                // Flash logic
                const prevStandings = prevStandingsRef.current;
                const newStatuses = {};
                let hasChanges = false;
                if (prevStandings && prevStandings.length > 0) {
                    newStandings.forEach((team, currentIndex) => {
                        const prevIndex = prevStandings.findIndex(p => p.name === team.name);
                        if (prevIndex !== -1 && currentIndex !== prevIndex) {
                            newStatuses[team.name] = currentIndex < prevIndex ? 'up' : 'down'; 
                            hasChanges = true;
                        }
                    });
                }

                prevStandingsRef.current = newStandings;
                setStandings(newStandings); 
                if (onDataLoaded) onDataLoaded(newStandings);
                if (hasChanges) {
                    setFlashStatuses(newStatuses);
                    setTimeout(() => setFlashStatuses({}), 1000);
                }
            } catch (err) {
                console.error("Fetch Error:", err);
                setError(t.error);
            } finally {
                setLoading(false);
            }
        };

        fetchTableAndFixturesData();
        const intervalId = setInterval(fetchTableAndFixturesData, 3000);
        return () => clearInterval(intervalId);
    }, [t.error, onDataLoaded]);

    if (loading && standings.length === 0) return <div className={styles.loading}>{t.loading}</div>;
    if (error) return <div className={styles.error}>{error}</div>;

    const getCircleClass = (outcome) => {
        if (outcome === 'W') return `${styles.formCircle} ${styles.win}`;
        if (outcome === 'D') return `${styles.formCircle} ${styles.draw}`;
        return `${styles.formCircle} ${styles.loss}`;
    };

    return (
        <div className={styles.container} style={{ direction: isRtl ? 'rtl' : 'ltr' }}>
            <h2 className={styles.title}>
                {t.standingsTitle || t.standings} 
                {currentRound ? ` | ${t.round || 'Round'} ${currentRound}` : ''}
            </h2>
            <table className={styles.table}>
                <thead>
                    <tr className={styles.headerRow}>
                        <th className={styles.th}>{t.rank}</th>
                        <th className={styles.th}>{t.team}</th>
                        <th className={styles.th}>{t.gf}</th>
                        <th className={styles.th}>{t.ga}</th>
                        <th className={styles.th}>{t.gd}</th>
                        <th className={styles.th}>{t.points}</th>
                        <th className={styles.th}>{t.form}</th>
                    </tr>
                </thead>
                <tbody>
                    {standings.map((team, index) => {
                        const status = flashStatuses[team.name];
                        let backgroundColor = status === 'up' ? 'rgba(46, 125, 50, 0.4)' : status === 'down' ? 'rgba(198, 40, 40, 0.4)' : 'transparent';
                        const localizedTeamName = t.teams?.[team.name] || team.name;
                        const history = teamForms[team.name] || [];

                        return (
                            <tr key={team.name} className={styles.tr} style={{ backgroundColor, transition: status ? 'none' : 'background-color 1s ease' }}>
                                <td className={styles.td}>{index + 1}</td>
                                <td className={`${styles.td} ${styles.teamName}`}>{localizedTeamName}</td>
                                <td className={styles.td}>{team.goalsScored}</td>
                                <td className={styles.td}>{team.goalsConceded}</td>
                                <td className={`${styles.goalDifference}`}>{team.goalsScored - team.goalsConceded}</td>
                                <td className={`${styles.points}`}>{team.points}</td>
                                <td className={styles.td}>
                                    <div className={styles.formContainer}>
                                        {history.map((outcome, i) => (
                                            <div key={i} className={getCircleClass(outcome)}>
                                                {t.outcomes[outcome]}
                                            </div>
                                        ))}
                                    </div>
                                </td>
                            </tr>
                        );
                    })}
                </tbody>
            </table>
        </div>
    );
}