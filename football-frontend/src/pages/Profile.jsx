import React, { useEffect, useState } from 'react';
import api from '../api/client';
import BetsHistory from '../components/BetsHistory';
import AccountInfo from '../components/AccountInfo';
import Settings from '../components/Settings';
import StandingsView from '../components/StandingsView';
import { translations } from '../utils/Translations';
import styles from '../styles/Profile.module.css';

export default function Profile({ globalLang, setGlobalLang }) {
    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [activeTab, setActiveTab] = useState('info');
    
    // Parse the translations directly using global parent props configuration
    const t = translations[globalLang];

    // Wallet Action States
    const [actionType, setActionType] = useState(null);
    const [walletAmount, setWalletAmount] = useState('');
    const [walletError, setWalletError] = useState('');

    useEffect(() => {
        const fetchProfileData = async () => {
            try {
                const response = await api.get('/user/profile');
                if (response.data && response.data.success) {
                    setProfile(response.data);
                } else {
                    setError(response.data?.message || 'Failed to load profile details.');
                }
            } catch (err) {
                setError('Unauthorized or network error. Please log in again.');
            } finally {
                setLoading(false);
            }
        };
        fetchProfileData();
    }, []);

    const handleWalletAction = async (e) => {
        e.preventDefault();
        setWalletError('');
        const amt = parseInt(walletAmount, 10);
        const currentBalance = profile?.balance || 0;

        if (isNaN(amt) || amt <= 0) {
            setWalletError('Please enter a valid whole number amount.');
            return;
        }
        if (actionType === 'withdraw' && amt > currentBalance) {
            setWalletError('Insufficient available funds for this withdrawal.');
            return;
        }

        try {
            const response = await api.post(`/user/wallet/${actionType}?amount=${amt}`);
            if (response.data && response.data.success) {
                setProfile(response.data);
                setActionType(null);
                setWalletAmount('');
            } else {
                setWalletError(response.data?.username === "INSUFFICIENT_FUNDS" ? 'Insufficient funds.' : 'Action failed.');
            }
        } catch (err) {
            setWalletError('Server connection error. Try again.');
        }
    };

    if (loading) {
        return <div className={styles.loading}>Loading profile...</div>;
    }

    if (error) {
        return <div className={styles.errorBanner}>{error}</div>;
    }

    const renderTabContent = () => {
        switch (activeTab) {
            case 'info':
                return (
                    <AccountInfo 
                        profile={profile}
                        actionType={actionType}
                        setActionType={setActionType}
                        walletAmount={walletAmount}
                        setWalletAmount={setWalletAmount}
                        walletError={walletError}
                        setWalletError={setWalletError}
                        handleWalletAction={handleWalletAction}
                        t={t}
                    />
                );
            case 'bets':
                return <BetsHistory bets={profile?.bets || []} t={t} />;
            case 'settings':
                return <Settings currentLang={globalLang} changeLang={setGlobalLang} />;
            default:
                return null;
        }
    };

    return (
        <div className={styles.profileContainer}>
            {/* Sub-navigation bar */}
            <div className={styles.subNavBar}>
                <button 
                    onClick={() => setActiveTab('info')}
                    className={activeTab === 'info' ? styles.navButtonActive : styles.navButton}
                >
                    {t.accountInfo}
                </button>
                <button 
                    onClick={() => setActiveTab('bets')}
                    className={activeTab === 'bets' ? styles.navButtonActive : styles.navButton}
                >
                    {t.betsHistory}
                </button>
                <button 
                    onClick={() => setActiveTab('settings')}
                    className={activeTab === 'settings' ? styles.navButtonActive : styles.navButton}
                >
                    {t.settings}
                </button>
            </div>

            {/* Dynamic Content Pane */}
            <div className={styles.contentPane}>
                {renderTabContent()}
            </div>
        </div>
    );
}