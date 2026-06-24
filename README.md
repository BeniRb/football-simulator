### 📊 Project Architecture & Interactivity Graph
```mermaid
graph TD
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
        E_User[User]
        E_Bet[Bet]
        E_Match[GameMatch]
        E_Team[Team]
        E_Settings[LeagueSettings]
        E_Token[RefreshToken]
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

    %% Structural Interactions
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

    Persist --> HbmXml
    AuthRepo --> HbmXml
    RefreshRepo --> HbmXml
    
    E_User --> HbmXml
    E_Bet --> HbmXml
    E_Match --> HbmXml
    E_Team --> HbmXml
    E_Settings --> HbmXml
    E_Token --> HbmXml

    ProfileService --> AuthRepo
    ProfileService --> BettingService
    ProfileService --> E_User
    ProfileService --> E_Bet

    CookieUtils --> Constants

    LoginResp --> BasicResp
    ProfileResp --> BasicResp
    BettingResp --> BasicResp
    LeagueResp --> BasicResp
    MatchSimResp --> BasicResp
    OddsResp --> BasicResp

    %% Click Interactions
    click JwtConfig "./football-backend/src/main/java/com/football/server/security/JwtConfig.java" "Go to JwtConfig"
    click JwtService "./football-backend/src/main/java/com/football/server/security/JwtService.java" "Go to JwtService"
    click AuthService "./football-backend/src/main/java/com/football/server/service/AuthService.java" "Go to AuthService"
    click TokenService "./football-backend/src/main/java/com/football/server/service/TokenService.java" "Go to TokenService"
    click UserService "./football-backend/src/main/java/com/football/server/service/UserService.java" "Go to UserService"
    click BettingService "./football-backend/src/main/java/com/football/server/service/BettingService.java" "Go to BettingService"
    click MatchSim "./football-backend/src/main/java/com/football/server/service/MatchSimulationService.java" "Go to MatchSimulationService"
    click OddsService "./football-backend/src/main/java/com/football/server/service/OddsService.java" "Go to OddsService"
    click LeagueService "./football-backend/src/main/java/com/football/server/service/LeagueService.java" "Go to LeagueService"
    click LeagueInit "./football-backend/src/main/java/com/football/server/service/LeagueInitializerService.java" "Go to LeagueInitializerService"
    click ProfileService "./football-backend/src/main/java/com/football/server/service/ProfileService.java" "Go to ProfileService"
    click Persist "./football-backend/src/main/java/com/football/server/service/Persist.java" "Go to Persist"
    
    click E_User "./football-backend/src/main/java/com/football/server/entities/User.java" "Go to User Entity"
    click E_Bet "./football-backend/src/main/java/com/football/server/entities/Bet.java" "Go to Bet Entity"
    click E_Match "./football-backend/src/main/java/com/football/server/entities/GameMatch.java" "Go to GameMatch Entity"
    click E_Team "./football-backend/src/main/java/com/football/server/entities/Team.java" "Go to Team Entity"
    click E_Settings "./football-backend/src/main/java/com/football/server/entities/LeagueSettings.java" "Go to LeagueSettings Entity"
    click E_Token "./football-backend/src/main/java/com/football/server/entities/RefreshToken.java" "Go to RefreshToken Entity"

    click HbmXml "./football-backend/src/main/resources/hibernate.cfg.xml" "Go to ORM Config XML"

    %% Styling Theme
    classDef components fill:#2d3139,stroke:#5c6370,stroke-width:1px,color:#abb2bf;
    class JwtConfig,JwtService,AuthService,UserService,TokenService,BettingService,MatchSim,OddsService,LeagueService,LeagueInit,ProfileService,Persist,E_User,E_Bet,E_Match,E_Team,E_Settings,E_Token,HbmXml,AuthRepo,RefreshRepo,Constants,CookieUtils,Errors,GenerateHash,BasicResp,LoginResp,ProfileResp,BettingResp,LeagueResp,MatchSimResp,OddsResp components;
