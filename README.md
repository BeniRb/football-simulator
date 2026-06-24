<img width="1536" height="1024" alt="ChatGPT Image Jun 24, 2026, 01_02_49 PM" src="https://github.com/user-attachments/assets/18faafe0-6c40-42f8-880c-334163a07f4b" />



Mathematical Model & Simulation Algorithm

The Football Simulator uses a probabilistic football model based on team skill ratings, environmental modifiers, and Poisson-distributed goal generation.

Odds Calculation

Before each round, the system calculates the probability of a Home Win, Draw, and Away Win using the skill difference between the two teams:

[
D = HomeSkill - AwaySkill
]

[
P(Home) = 0.45 + 0.007D
]

[
P(Away) = 0.30 - 0.007D
]

[
P(Draw) = 0.25 - 0.002|D|
]

The probabilities are then converted into decimal betting odds using a bookmaker margin:

[
Odds = \frac{1.10}{Probability}
]

This creates realistic sportsbook-style odds while maintaining a house edge.

Match Simulation

For each team, the simulator calculates an expected goals value ((\lambda)):

[
\lambda = 1.5 + 0.2(Sa-Sd) + H + W + I
]

Where:

(Sa) = attacking team's skill.
(Sd) = defending team's skill.
(H) = home advantage bonus.
(W) = weather modifier.
(I) = injury modifier.

Weather and injury events are generated randomly before the match begins:

Sunny: 70%
Rainy: 20%
Stormy: 10%
Injury event: 15%
Poisson Goal Generation

Once (\lambda) is calculated, the simulator uses the Poisson distribution:

[
P(X=k)=\frac{e^{-\lambda}\lambda^k}{k!}
]

to model the probability of scoring exactly (k) goals.

The system then applies Knuth's Poisson Random Sampling Algorithm to generate an actual goal count from the distribution. This process is executed independently for both teams.

Winner Determination

The final score is produced by the Poisson simulation:

Home Goals = Poisson(λ_home)
Away Goals = Poisson(λ_away)

The match outcome is then determined by comparing the generated scores:

Home Goals > Away Goals → HOME_WIN
Away Goals > Home Goals → AWAY_WIN
Home Goals = Away Goals → DRAW
Bet Settlement

When a user places a bet, the wager is locked until the match is completed. Winning payouts are calculated using the odds recorded at placement time:

[
Payout = Wager \times Odds_{placement}
]

[
Profit = Payout - Wager
]

This ensures that odds changes after bet placement do not affect previously accepted wagers.

Simulation Flow
Team Skills
     ↓
Probability Calculation
     ↓
Odds Generation
     ↓
Weather & Injury Events
     ↓
Expected Goals (λ)
     ↓
Poisson Distribution
     ↓
Knuth Poisson Sampling
     ↓
Generated Score
     ↓
Match Result
     ↓
Bet Settlement

This combination of probability theory, Poisson distributions, random sampling, and sportsbook mathematics creates a realistic football simulation and betting environment.
