import React, { useState } from 'react';
import styles from '../styles/BetsHistory.module.css';

const BetsHistory = ({ bets = [], t }) => {
    const [filter, setFilter] = useState('ALL');

    const filteredBets = bets.filter(bet => {
        if (filter === 'ACTIVE') return !bet.isSettled;
        if (filter === 'SETTLED') return bet.isSettled;
        return true;
    });

    const getPredictionLabel = (type) => {
        if (type === 'HOME_WIN') return t.homeWin;
        if (type === 'AWAY_WIN') return t.awayWin;
        return t.draw;
    };

    return (
        <div>
            {/* Filter Controls */}
            <div className={styles.filterRow}>
                {['ALL', 'ACTIVE', 'SETTLED'].map((type) => (
                    <button
                        key={type}
                        onClick={() => setFilter(type)}
                        className={filter === type ? styles.filterBtnActive : styles.filterBtn}
                    >
                        {type === 'ALL' ? (t.noBets.includes('לא') ? 'הכל' : 'All') : 
                         type === 'ACTIVE' ? (t.noBets.includes('לא') ? 'פעיל' : 'Active') : 
                         (t.noBets.includes('לא') ? 'הסתיים' : 'Settled')}
                    </button>
                ))}
            </div>

            {/* Matrix Table */}
            {filteredBets.length === 0 ? (
                <p className={styles.emptyText}>{t.noBets}</p>
            ) : (
                <table className={styles.historyTable}>
                    <thead>
                        <tr className={styles.headerRow}>
                            <th className={styles.tableTh}>{t.slipId}</th>
                            <th className={styles.tableTh}>{t.predictionTarget}</th>
                            <th className={styles.tableTh}>{t.stake}</th>
                            <th className={styles.tableTh}>{t.odds}</th>
                            <th className={styles.tableTh}>{t.status}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {filteredBets.map((bet) => (
                            <tr key={bet.id} className={styles.dataRow}>
                                <td className={styles.tableTdMuted}>#{bet.id}</td>
                                <td className={styles.tableTd}>
                                    <strong className={styles.blockLabel}>{getPredictionLabel(bet.prediction)}</strong>
                                    <span className={styles.subLabel}>{t.slipId} #{bet.matchId}</span>
                                </td>
                                <td className={styles.tableTd}>${bet.wagerAmount}</td>
                                <td className={styles.tableTd}>x{bet.oddsAtPlacement.toFixed(2)}</td>
                                <td className={styles.tableTd}>
                                    {!bet.isSettled ? (
                                        <span className={styles.statusRunning}>{t.running}</span>
                                    ) : bet.isWin ? (
                                        <span className={styles.statusWin}>
                                            {t.won} (+${Math.round(bet.wagerAmount * bet.oddsAtPlacement) - bet.wagerAmount})
                                        </span>
                                    ) : (
                                        <span className={styles.statusLoss}>
                                            {t.lost} (-${bet.wagerAmount})
                                        </span>
                                    )}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
};

export default BetsHistory;