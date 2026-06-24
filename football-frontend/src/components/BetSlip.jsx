import React, { useState } from 'react';
import api from '../api/client';
import { translations } from '../utils/Translations';
import styles from '../styles/BetSlip.module.css';

export default function BetSlip({ language, selectedBet, clearBet, onBetPlaced }) {
    const [wagerAmount, setWagerAmount] = useState('');
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState({ text: '', isError: false });

    const activeLang = language === 'HE' ? 'HE' : 'EN';
    const t = translations[activeLang] || translations.EN || {};
    const isRtl = activeLang === 'HE';

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
                /* This placeholder shows when no match has been selected yet */
                <div style={{ textAlign: 'center', color: '#888', padding: '20px 0' }}>
                    {isRtl ? 'בחר משחק מהלוח כדי להמר' : 'Select a match from the dashboard to place a bet'}
                </div>
            ) : (
                /* Main betting elements render once an odd button is active */
                <>
                    <div className={styles.matchDetails}>
                        <div className={styles.teamsRow}>
                            {selectedBet.homeName} {t.vs || (isRtl ? 'נגד' : 'vs')} {selectedBet.awayName}
                        </div>
                        <div className={styles.selectionRow}>
                            <span>{t.selectionLabel || (isRtl ? 'בחירה:' : 'Selection:')} <strong>{getPredictionLabel(selectedBet.predictionType)}</strong></span>
                            <span className={styles.oddsValue}>@{selectedBet.odds}</span>
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