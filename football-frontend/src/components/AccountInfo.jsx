import React from 'react';
import styles from '../styles/AccountInfo.module.css';

export default function AccountInfo({ profile, actionType, setActionType, walletAmount, setWalletAmount, walletError, setWalletError, handleWalletAction, t }) {
    const balance = profile?.balance || 0;
    const lockedBalance = profile?.lockedBalance || 0;
    const wins = profile?.wins || 0;
    const losses = profile?.losses || 0;
    const netProfitLoss = profile?.netProfitLoss || 0;
    const activeBetsCount = profile?.activeBetsCount || 0;
    const isProfitable = netProfitLoss >= 0;

    return (
        <div className={styles.container}>
            {/* User Credentials Row */}
            <div className={styles.credentialsRow}>
                <div>
                    <p className={styles.subLabel}>{t.username}</p>
                    <strong className={styles.strongValue}>{profile?.username}</strong>
                </div>
                <div className={styles.textAlignRight}>
                    <p className={styles.subLabel}>{t.email}</p>
                    <strong className={styles.strongValue}>{profile?.email}</strong>
                </div>
            </div>

            {/* Financial Wallet Dashboard Box */}
            <div className={styles.dashboardBox}>
                <h3 className={styles.boxHeading}>{t.walletHeading}</h3>
                
                <div className={styles.flexRowSpaceMargin}>
                    <div>
                        <span className={styles.labelNormal}>{t.available}</span>
                        <h2 className={styles.valueLargeGreen}>${balance}</h2>
                    </div>
                    <div className={styles.textAlignRight}>
                        <span className={styles.labelNormal}>{t.inPlay}</span>
                        <h2 className={styles.valueLargeYellow}>${lockedBalance}</h2>
                    </div>
                </div>

                {/* Action Control Buttons Row */}
                <div className={`${styles.buttonRow} ${actionType ? styles.marginBottom : ''}`}>
                    <button 
                        onClick={() => { setActionType(actionType === 'deposit' ? null : 'deposit'); setWalletError(''); }}
                        className={actionType === 'deposit' ? styles.depositBtnActive : styles.depositBtn}
                    >
                        {actionType === 'deposit' ? t.cancelDeposit : t.depositFunds}
                    </button>
                    <button 
                        onClick={() => { setActionType(actionType === 'withdraw' ? null : 'withdraw'); setWalletError(''); }}
                        className={actionType === 'withdraw' ? styles.withdrawBtnActive : styles.withdrawBtn}
                    >
                        {actionType === 'withdraw' ? t.cancelWithdraw : t.withdrawFunds}
                    </button>
                </div>

                {/* Contextual Inline Form Area */}
                {actionType && (
                    <form onSubmit={handleWalletAction} className={styles.inlineForm}>
                        <label className={styles.formLabel}>{t.formPrompt}</label>
                        <div className={styles.inputGroup}>
                            <input 
                                type="number" 
                                step="1"
                                value={walletAmount}
                                onChange={(e) => setWalletAmount(e.target.value)}
                                placeholder="e.g. 50" 
                                className={styles.numberInput}
                            />
                            <button type="submit" className={styles.submitBtn}>
                                {t.confirm}
                            </button>
                        </div>
                        {walletError && <p className={styles.errorText}>{walletError}</p>}
                    </form>
                )}
            </div>

            {/* Betting Performance Card */}
            <div className={styles.dashboardBox}>
                <h3 className={styles.boxHeading}>{t.analyticsHeading}</h3>
                
                {/* Row 1: Performance Trackers */}
                <div className={styles.alignCenterMargin}>
                    <div>
                        <span className={styles.labelNormal}>{t.recordLabel}</span>
                        <h3 className={styles.valueMedium}>
                            <span className={styles.valueMediumWin}>{wins}W</span>
                            <span className={styles.labelNormal} style={{ margin: '0 5px' }}>-</span>
                            <span className={styles.valueMediumLoss}>{losses}L</span>
                        </h3>
                    </div>
                    <div className={styles.textAlignRight}>
                        <span className={styles.labelNormal}>{t.activeSlips}</span>
                        <h3 className={styles.valueMediumYellow}>
                            {activeBetsCount} {t.running}
                        </h3>
                    </div>
                </div>

                {/* Accent Divider Line */}
                <div className={styles.divider}></div>

                {/* Row 2: Financial Yield Summary */}
                <div className={styles.alignCenter}>
                    <div>
                        <span className={styles.labelSmall}>{t.accStatus}</span>
                        <p className={styles.textNormal}>{t.verified}</p>
                    </div>
                    <div className={styles.textAlignRight}>
                        <span className={styles.labelNormal}>{t.netProfit}</span>
                        <h3 className={isProfitable ? styles.valueProfit : styles.valueLoss}>
                            {isProfitable ? `+$${netProfitLoss}` : `-$${Math.abs(netProfitLoss)}`}
                        </h3>
                    </div>
                </div>
            </div>
        </div>
    );
}