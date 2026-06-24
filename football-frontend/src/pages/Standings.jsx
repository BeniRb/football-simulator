import React from 'react';
import StandingsView from '../components/StandingsView';
import MatchLog from '../components/MatchLog';

export default function Standings({ globalLang }) {
    return (
        <div>
            <StandingsView language={globalLang} />
            <MatchLog language={globalLang} />
        </div>
    );
}