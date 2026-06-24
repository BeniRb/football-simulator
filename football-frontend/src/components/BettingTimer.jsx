import React, { useEffect, useState } from 'react';
import api from '../api/client';
import styles from '../styles/BettingTimer.module.css';

export default function BettingTimer({ language }) {
    const [secondsLeft, setSecondsLeft] = useState(20);
    const [windowOpen, setWindowOpen] = useState(true);

    const isRtl = language === 'HE';

    useEffect(() => {
        const fetchSettings = async () => {
            try {
                const res = await api.get('/league/settings');

                if (res.data?.success && res.data?.data) {
                    const open = res.data.data.bettingWindowOpen;
                    const elapsed = res.data.data.secondsElapsedInRound || 0;

                    setWindowOpen(open);
                    setSecondsLeft(open ? Math.max(0, 20 - elapsed) : 0);
                }
            } catch (err) {
                console.error('Timer settings error:', err);
            }
        };

        fetchSettings();
        const settingsInterval = setInterval(fetchSettings, 3000);

        return () => clearInterval(settingsInterval);
    }, []);

    useEffect(() => {
        if (!windowOpen) {
            setSecondsLeft(0);
            return;
        }

        const countdown = setInterval(() => {
            setSecondsLeft(prev => Math.max(0, prev - 1));
        }, 1000);

        return () => clearInterval(countdown);
    }, [windowOpen]);

    return (
        <div className={styles.timerBox} dir={isRtl ? 'rtl' : 'ltr'}>
            {windowOpen
                ? isRtl
                    ? `חלון ההימורים נסגר בעוד ${secondsLeft} שניות`
                    : `Betting closes in ${secondsLeft}s`
                : isRtl
                    ? 'חלון ההימורים סגור'
                    : 'Betting window closed'}
        </div>
    );
}