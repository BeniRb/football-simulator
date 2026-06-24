import React, { useState } from 'react';
import LiveDashboard from '../components/LiveDashboard';
import BetSlip from '../components/BetSlip';

export default function Dashboard({ globalLang }) {
    const [activeBet, setActiveBet] = useState(null);

    return (
        <div className="dashboard-page" style={{ display: 'block', width: '100%' }}>
            <LiveDashboard 
                language={globalLang} 
                onSelectBet={setActiveBet} 
                selectedBet={activeBet} 
            />
            <BetSlip 
                language={globalLang} 
                selectedBet={activeBet} 
                clearBet={() => setActiveBet(null)} 
            />
        </div>
    );
}