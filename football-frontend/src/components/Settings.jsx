import React, { useState, useEffect } from 'react';
import { translations } from '../utils/Translations';
import styles from '../styles/Settings.module.css';

export default function Settings({ currentLang, changeLang }) {
    const t = translations[currentLang];

    const [oddsFormat, setOddsFormat] = useState('DECIMAL');
    const [theme, setTheme] = useState(() => {
        return document.body.getAttribute('data-theme') || 'DARK';
    });
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [message, setMessage] = useState('');

    useEffect(() => {
        document.body.setAttribute('data-theme', theme);
    }, [theme]);

    const handleSavePreferences = (e) => {
        e.preventDefault();
        setMessage(t.successMsg);
        setTimeout(() => setMessage(''), 3000);
    };

    return (
        <div className={styles.container}>
            {/* Display Preferences Card */}
            <div className={styles.section}>
                <h3 className={styles.heading}>{t.displayPrefs}</h3>
                <form onSubmit={handleSavePreferences}>
                    <div className={styles.formGroup} style={{ marginBottom: '15px' }}>
                        <label className={styles.label}>{t.langSelect}</label>
                        <select value={currentLang} onChange={(e) => changeLang(e.target.value)}className={styles.select}>
                            <option value="EN">English</option>
                            <option value="HE">עברית (Hebrew)</option>
                        </select>
                    </div>

                    <div className={styles.formGroup} style={{ marginBottom: '15px' }}>
                        <label className={styles.label}>{t.interfaceTheme}</label>
                        <select value={theme} onChange={(e) => setTheme(e.target.value)}className={styles.select}>
                            <option value="DARK">Dark Mode</option>
                            <option value="LIGHT">Light Mode</option>
                        </select>
                    </div>

                    <div className={styles.formGroup}>
                        <label className={styles.label}>{t.oddsFormat}</label>
                        <select value={oddsFormat} onChange={(e) => setOddsFormat(e.target.value)} className={styles.select}>
                            <option value="DECIMAL">Decimal Format (2.50)</option>
                            <option value="AMERICAN">American Format (+150)</option>
                        </select>
                    </div>
                    <button type="submit" className={styles.saveBtn} style={{ marginTop: '10px' }}>
                        {t.savePrefs}
                    </button>
                    {message && <p className={styles.successText}>{message}</p>}
                </form>
            </div>

            {/* Security Settings Card */}
            <div className={styles.section}>
                <h3 className={styles.heading}>{t.security}</h3>
                <div className={styles.formGroup}>
                    <label className={styles.label}>{t.newPassword}</label>
                    <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="••••••••" className={styles.input}/>
                </div>
                <div className={styles.formGroup}>
                    <label className={styles.label}>{t.confirmPassword}</label>
                    <input type="password" value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} placeholder="••••••••" className={styles.input}
                    />
                </div>
                <button className={styles.saveBtn} style={{ backgroundColor: '#3a3a3a', border: '1px solid var(--border)' }}>
                    {t.updatePassword}
                </button>
            </div>
        </div>
    );
}