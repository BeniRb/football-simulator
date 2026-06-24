<img width="1536" height="1024" alt="ChatGPT Image Jun 24, 2026, 01_02_49 PM" src="https://github.com/user-attachments/assets/18faafe0-6c40-42f8-880c-334163a07f4b" />



## Mathematical Model & Simulation Algorithm

The Football Simulator uses a probabilistic football model based on team skill ratings, environmental modifiers, and Poisson-distributed goal generation.

### Odds Calculation

Before each round, the system calculates the probability of a Home Win, Draw, and Away Win using the skill difference between the two teams.

```text
D = HomeSkill - AwaySkill

P(Home) = 0.45 + 0.007 × D

P(Away) = 0.30 - 0.007 × D

P(Draw) = 0.25 - 0.002 × |D|
```

The probabilities are then converted into decimal betting odds using a bookmaker margin:

```text
Odds = 1.10 / Probability
```

This creates realistic sportsbook-style odds while maintaining a house edge.

### Match Simulation

For each team, the simulator calculates an expected goals value (λ):

```text
λ = 1.5 + 0.2 × (AttackerSkill - DefenderSkill)
    + HomeAdvantage
    + WeatherModifier
    + InjuryModifier
```

Where:

| Variable        | Description                       |
| --------------- | --------------------------------- |
| AttackerSkill   | Skill level of the attacking team |
| DefenderSkill   | Skill level of the defending team |
| HomeAdvantage   | +0.3 for the home team            |
| WeatherModifier | 0, -0.2, or -0.4                  |
| InjuryModifier  | 0 or -0.4                         |

Random events generated before each match:

| Event        | Probability |
| ------------ | ----------- |
| Sunny        | 70%         |
| Rainy        | 20%         |
| Stormy       | 10%         |
| Injury Event | 15%         |

### Poisson Goal Generation

Once λ is calculated, the simulator uses the Poisson distribution:

```text
P(X = k) = (e^-λ × λ^k) / k!
```

Where:

* X = number of goals
* k = exact goal count
* λ = expected goals

The system then applies **Knuth's Poisson Random Sampling Algorithm** to generate an actual goal count from the distribution.

This process is executed independently for both teams.

### Winner Determination

The final score is generated using Poisson sampling:

```text
HomeGoals = Poisson(λ_home)

AwayGoals = Poisson(λ_away)
```

The winner is determined by comparing the generated scores:

```text
HomeGoals > AwayGoals  → HOME_WIN

AwayGoals > HomeGoals  → AWAY_WIN

HomeGoals = AwayGoals  → DRAW
```

### Bet Settlement

When a user places a bet, the wager is locked until the match is completed.

Winning payouts are calculated using the odds stored when the bet was placed:

```text
Payout = Wager × OddsAtPlacement

Profit = Payout - Wager
```

This ensures that odds changes after bet placement do not affect previously accepted wagers.

### Simulation Flow

```text
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
```

This combination of probability theory, Poisson distributions, random sampling, and sportsbook mathematics creates a realistic football simulation and betting environment.
