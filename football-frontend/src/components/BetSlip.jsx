import React, { useState, useEffect } from 'react';
import api from '../api/client';
import { translations } from '../utils/Translations';
import styles from '../styles/BetSlip.module.css';

export default function BetSlip({ language, selectedBet, clearBet, onBetPlaced }) {
    const [wagerAmount, setWagerAmount] = useState('');
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState({ text: '', isError: false });
    const [oddsFormat, setOddsFormat] = useState(() => {
        return localStorage.getItem('oddsFormat') || 'DECIMAL';
    });

    const activeLang = language === 'HE' ? 'HE' : 'EN';
    const t = translations[activeLang] || translations.EN || {};
    const isRtl = activeLang === 'HE';

    useEffect(() => {
        const syncOddsFormat = () => {
            setOddsFormat(localStorage.getItem('oddsFormat') || 'DECIMAL');
        };

        syncOddsFormat();

        window.addEventListener('oddsFormatChanged', syncOddsFormat);
        window.addEventListener('storage', syncOddsFormat);

        return () => {
            window.removeEventListener('oddsFormatChanged', syncOddsFormat);
            window.removeEventListener('storage', syncOddsFormat);
        };
    }, []);

    const formatOdds = (decimalOdds) => {
        const odds = Number(decimalOdds);

        if (!odds || Number.isNaN(odds)) {
            return '-';
        }

        if (oddsFormat === 'AMERICAN') {
            if (odds >= 2) {
                return `+${Math.round((odds - 1) * 100)}`;
            }

            return `${Math.round(-100 / (odds - 1))}`;
        }

        return odds.toFixed(2);
    };

    const getPredictionLabel = (type) => {
        if (type === 'home') return t.homeWin || (isRtl ? 'ניצחון בית' : 'Home Win');
        if (type === 'draw') return t.draw || (isRtl ? 'תיקו' : 'Draw');
        return t.awayWin || (isRtl ? 'ניצחון חוץ' : 'Away Win');
    };

    const getPredictionBackendValue = (type) => {
        if (type === 'home') return 'HOME_WIN';
        if (type === 'draw') return 'DRAW';
        return 'AWAY_WIN';
    };

    const handlePlaceBet = async (e) => {
        e.preventDefault();
        setMessage({ text: '', isError: false });

        const amount = parseInt(wagerAmount, 10);
        if (isNaN(amount) || amount <= 0) {
            setMessage({
                text: t.invalidAmountError || (isRtl ? 'הזן סכום תקין' : 'Please enter a valid amount.'),
                isError: true
            });
            return;
        }

        setLoading(true);

        try {
            const payload = {
                matchId: selectedBet.matchId,
                prediction: getPredictionBackendValue(selectedBet.predictionType),
                wagerAmount: amount,

                // Important:
                // Keep this as decimal odds for backend validation and payout math.
                clientOdds: selectedBet.odds
            };

            const response = await api.post('/bets/place', payload);

            if (response.data && response.data.success) {
                setMessage({
                    text: t.betPlacedSuccess || (isRtl ? 'ההימור שובץ בהצלחה!' : 'Bet placed successfully!'),
                    isError: false
                });

                setWagerAmount('');

                if (onBetPlaced) onBetPlaced();

                setTimeout(() => clearBet(), 1500);
            } else {
                setMessage({
                    text: response.data?.message || t.betPlacedError || (isRtl ? 'שיבוץ ההימור נכשל.' : 'Bet placement failed.'),
                    isError: true
                });
            }
        } catch (err) {
            console.error("Bet placement error:", err);
            setMessage({
                text: t.networkError || (isRtl ? 'שגיאת חיבור ברשת.' : 'Network connection error.'),
                isError: true
            });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className={styles.slipContainer} style={{ direction: isRtl ? 'rtl' : 'ltr' }}>
            <div className={styles.slipHeader}>
                <h3>{t.betSlipTitle || (isRtl ? 'טופס הימור' : 'Bet Slip')}</h3>
                {selectedBet && <button className={styles.clearBtn} onClick={clearBet}>×</button>}
            </div>

            {!selectedBet ? (
                <div style={{ textAlign: 'center', color: 'var(--text)', padding: '20px 0' }}>
                    {isRtl ? 'בחר משחק מהלוח כדי להמר' : 'Select a match from the dashboard to place a bet'}
                </div>
            ) : (
                <>
                    <div className={styles.matchDetails}>
                        <div className={styles.teamsRow}>
                            {selectedBet.homeName} {t.vs || (isRtl ? 'נגד' : 'vs')} {selectedBet.awayName}
                        </div>

                        <div className={styles.selectionRow}>
                            <span>
                                {t.selectionLabel || (isRtl ? 'בחירה:' : 'Selection:')}{' '}
                                <strong>{getPredictionLabel(selectedBet.predictionType)}</strong>
                            </span>

                            <span
                                className={styles.oddsValue}
                                dir="ltr"
                                style={{ unicodeBidi: 'isolate' }}
                            >
                                {formatOdds(selectedBet.odds)}
                            </span>
                        </div>
                    </div>

                    <form onSubmit={handlePlaceBet} className={styles.betForm}>
                        <div className={styles.inputWrapper}>
                            <input
                                type="number"
                                placeholder={t.wagerPlaceholder || (isRtl ? 'סכום הימור' : 'Wager Amount ($)')}
                                value={wagerAmount}
                                onChange={(e) => setWagerAmount(e.target.value)}
                                disabled={loading}
                            />

                            <button type="submit" disabled={loading} className={styles.submitBetBtn}>
                                {loading
                                    ? (t.processingLabel || (isRtl ? 'מעבד...' : 'Processing...'))
                                    : (t.placeBetBtn || (isRtl ? 'שלח הימור' : 'Place Bet'))}
                            </button>
                        </div>
                    </form>
                </>
            )}

            {message.text && (
                <div className={message.isError ? styles.errorMsg : styles.successMsg}>
                    {message.text}
                </div>
            )}
        </div>
    );
}