### 📊 Project Architecture & Interactivity Graph

```mermaid
graph LR
    %% Set layout direction from Left to Right
    
    %% Package Layer Groups
    subgraph Security ["🔐 Security Layer"]
        JwtConfig[JwtConfig.java]
        JwtService[JwtService.java]
    end

    subgraph Auth ["⚙️ Core Authentication Services"]
        AuthService[AuthService.java]
        UserService[UserService.java]
        TokenService[TokenService.java]
    end

    subgraph Sportsbook ["⚽ Sportsbook & Simulation"]
        BettingService[BettingService.java]
        MatchSim[MatchSimulationService.java]
        OddsService[OddsService.java]
        LeagueService[LeagueService.java]
        LeagueInit[LeagueInitializerService.java]
    end

    subgraph Storage ["🗄️ Database Entities & ORM"]
        E_User(User)
        E_Bet(Bet)
        E_Match(GameMatch)
        E_Team(Team)
        E_Settings(LeagueSettings)
        E_Token(RefreshToken)
    end

    subgraph Infrastructure ["💾 Persistence & Utilities"]
        Persist[Persist.java]
        AuthRepo[AuthRepository]
        RefreshRepo[RefreshTokenRepository]
        HbmXml[hibernate.cfg.xml]
        ProfileService[ProfileService.java]
        CookieUtils[CookieUtils.java]
        Constants[Constants.java]
        Errors[Errors.java]
        GenerateHash[GenerateHash.java]
    end

    subgraph DTOs ["📦 Response DTO Hierarchy"]
        BasicResp[BasicResponse.java]
        LoginResp[LoginResponse.java]
        ProfileResp[ProfileResponse.java]
        BettingResp[BettingResponse.java]
        LeagueResp[LeagueResponse.java]
        MatchSimResp[MatchSimulationResponse.java]
        OddsResp[OddsResponse.java]
    end

    %% Enforce linear subgraph placement to stop messy overlapping
    Security --> Auth --> Sportsbook --> Storage --> Infrastructure

    %% Structural Interactions & Dependencies
    JwtConfig --> JwtService
    JwtService --> Constants
    AuthService --> GenerateHash
    AuthService --> UserService
    AuthService --> TokenService
    TokenService --> RefreshRepo
    TokenService --> AuthRepo
    TokenService --> JwtService
    TokenService --> E_Token
    TokenService --> E_User
    UserService --> AuthRepo
    UserService --> E_User

    BettingService --> Persist
    BettingService --> JwtService
    BettingService --> E_User
    BettingService --> E_Bet
    BettingService --> E_Match
    BettingService --> E_Settings

    MatchSim --> Persist
    MatchSim --> BettingService
    MatchSim --> OddsService
    MatchSim --> E_Team
    MatchSim --> E_Match
    MatchSim --> E_Settings

    OddsService --> Persist
    OddsService --> E_Team
    OddsService --> E_Match

    LeagueService --> Persist
    LeagueService --> E_Match
    LeagueService --> E_Team
    LeagueService --> E_Settings

    LeagueInit --> Persist
    LeagueInit --> E_Team
    LeagueInit --> E_Match
    LeagueInit --> E_Settings

    Persist & AuthRepo & RefreshRepo --> HbmXml
    E_User & E_Bet & E_Match & E_Team & E_Settings & E_Token --> HbmXml

    ProfileService --> AuthRepo
    ProfileService --> BettingService
    ProfileService --> E_User
    ProfileService --> E_Bet

    CookieUtils --> Constants
    Errors --> AuthService & BettingService & LeagueService & MatchSim & OddsService

    LoginResp & ProfileResp & BettingResp & LeagueResp & MatchSimResp & OddsResp --> BasicResp

    %% Click Interactions
    click JwtConfig "./backend/src/com/football/server/security/JwtConfig.java" "Go to JwtConfig"
    click JwtService "./backend/src/com/football/server/security/JwtService.java" "Go to JwtService"
    click AuthService "./backend/src/com/football/server/service/AuthService.java" "Go to AuthService"
    click TokenService "./backend/src/com/football/server/service/TokenService.java" "Go to TokenService"
    click UserService "./backend/src/com/football/server/service/UserService.java" "Go to UserService"
    click BettingService "./backend/src/com/football/server/service/BettingService.java" "Go to BettingService"
    click MatchSim "./backend/src/com/football/server/service/MatchSimulationService.java" "Go to MatchSimulationService"
    click OddsService "./backend/src/com/football/server/service/OddsService.java" "Go to OddsService"
    click LeagueService "./backend/src/com/football/server/service/LeagueService.java" "Go to LeagueService"
    click LeagueInit "./backend/src/com/football/server/service/LeagueInitializerService.java" "Go to LeagueInitializerService"
    click ProfileService "./backend/src/com/football/server/service/ProfileService.java" "Go to ProfileService"
    click Persist "./backend/src/com/football/server/service/Persist.java" "Go to Persist"
    
    click E_User "./backend/src/com/football/server/entities/User.java" "Go to User Entity"
    click E_Bet "./backend/src/com/football/server/entities/Bet.java" "Go to Bet Entity"
    click E_Match "./backend/src/com/football/server/entities/GameMatch.java" "Go to GameMatch Entity"
    click E_Team "./backend/src/com/football/server/entities/Team.java" "Go to Team Entity"
    click E_Settings "./backend/src/com/football/server/entities/LeagueSettings.java" "Go to LeagueSettings Entity"
    click E_Token "./backend/src/com/football/server/entities/RefreshToken.java" "Go to RefreshToken Entity"

    click HbmXml "./backend/src/hibernate.cfg.xml" "Go to ORM Config XML"

    %% Styling Theme
    classDef components fill:#2d3139,stroke:#5c6370,stroke-width:1px,color:#abb2bf;
    class JwtConfig,JwtService,AuthService,UserService,TokenService,BettingService,MatchSim,OddsService,LeagueService,LeagueInit,ProfileService,Persist,E_User,E_Bet,E_Match,E_Team,E_Settings,E_Token,HbmXml,AuthRepo,RefreshRepo,Constants,CookieUtils,Errors,GenerateHash,BasicResp,LoginResp,ProfileResp,BettingResp,LeagueResp,MatchSimResp,OddsResp components;
