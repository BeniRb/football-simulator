import React from 'react';

export default function Home() {
    return (
        <div style={{ padding: "30px", maxWidth: "900px", margin: "0 auto" }}>
            <h1>Welcome to the Virtual Football League</h1>
            <p style={{ fontSize: "1.2rem", color: "#666", lineHeight: "1.6" }}>
                Experience a completely simulated football ecosystem driven by real team statistics, 
                dynamic live match engine progressions, and fluctuating betting odds algorithms.
            </p>
            <hr style={{ margin: "25px 0", borderColor: "#ddd" }} />
            <h3>Features:</h3>
            <ul>
                <li><strong>Live Match Dashboards:</strong> Track live text updates and match events shifting every 30 seconds.</li>
                <li><strong>Dynamic Odds:</strong> Place calculated bets based on real shifting statistical team skills.</li>
                <li><strong>Global Standings:</strong> Monitor tournament points, goal differences, and historical leaderboards.</li>
            </ul>
        </div>
    );
}