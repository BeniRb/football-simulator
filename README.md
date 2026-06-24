# ⚽ Virtual Football Sportsbook & Match Simulator

Automated full-stack football betting platform built with a Java Spring Boot backend engine and a dynamic React frontend. The application features real-time background match simulation, dynamic oddsmaking algorithms, and secure JWT token session validation.

---

## 📂 Project Repository Structure

This project is organized as a monorepo, keeping both the frontend client and the backend server code synchronized inside a single repository:

```text
football-project/
│
├── football-backend/      <-- Spring Boot Application Core
│   ├── src/main/java/     <-- Java Package Layer Blueprints
│   ├── src/main/resources/<-- Application Properties & Hibernate XML
│   └── pom.xml            <-- Maven Dependency Management
│
├── frontend/              <-- React User Interface (Vite / CRA)
│   ├── src/               <-- UI Components, Hooks, & Styling
│   └── package.json       <-- Node Dependency Scripts
│
└── README.md              <-- System Overview Index

graph TD
    %% --- LAYER DIRECTORIES ---
    Sec_Title[🔐 SECURITY LAYER] === JwtConfig[JwtConfig.java]
    JwtConfig --> JwtService[JwtService.java]

    Auth_Title[⚙️ AUTH SERVICES] === AuthService[AuthService.java]
    AuthService --> GenerateHash[GenerateHash.java]
    AuthService --> UserService[UserService.java]
    AuthService --> TokenService[TokenService.java]

    Sport_Title[⚽ SPORTSBOOK & SIMULATION] === BettingService[BettingService.java]
    MatchSim[MatchSimulationService.java]
    OddsService[OddsService.java]
    LeagueService[LeagueService.java]
    LeagueInit[LeagueInitializerService.java]

    Store_Title[🗄️ DATABASE ENTITIES] === E_User(User)
    E_Bet(Bet)
    E_Match(GameMatch)
    E_Team(Team)
    E_Settings(LeagueSettings)
    E_Token(RefreshToken)

    Infra_Title[💾 PERSISTENCE & INFRASTRUCTURE] === Persist[Persist.java]
    AuthRepo[AuthRepository]
    RefreshRepo[RefreshTokenRepository]
    HbmXml[hibernate.cfg.xml]
    ProfileService[ProfileService.java]
    CookieUtils[CookieUtils.java]
    Constants[Constants.java]

    Dto_Title[📦 RESPONSE DTOS] === BasicResp[BasicResponse.java]
    LoginResp[LoginResponse.java]
    ProfileResp[ProfileResponse.java]
    BettingResp[BettingResponse.java]
    LeagueResp[LeagueResponse.java]
    MatchSimResp[MatchSimulationResponse.java]
    OddsResp[OddsResponse.java]

    %% --- SYSTEM FLOWS ---
    JwtService -.-> TokenService
    TokenService --> RefreshRepo
    TokenService --> AuthRepo
    TokenService --> E_Token
    TokenService --> E_User
    UserService --> AuthRepo

    ProfileService --> AuthRepo
    ProfileService --> BettingService
    ProfileService --> E_User
    ProfileService --> E_Bet

    BettingService --> Persist
    BettingService --> E_User
    BettingService --> E_Bet
    BettingService --> E_Match
    
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
    LeagueInit --> Persist

    Persist --> HbmXml
    AuthRepo --> HbmXml
    RefreshRepo --> HbmXml
    
    E_User --> HbmXml
    E_Bet --> HbmXml
    E_Match --> HbmXml
    E_Team --> HbmXml
    E_Settings --> HbmXml
    E_Token --> HbmXml
    
    CookieUtils --> Constants

    LoginResp --> BasicResp
    ProfileResp --> BasicResp
    BettingResp --> BasicResp
    LeagueResp --> BasicResp
    MatchSimResp --> BasicResp
    OddsResp --> BasicResp

    %% --- INTERACTIVE ROUTING LINKS ---
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

    %% --- VISUAL COMPONENT STYLES ---
    classDef files fill:#2d3139,stroke:#5c6370,stroke-width:1px,color:#abb2bf;
    classDef titles fill:#1e222a,stroke:#abb2bf,stroke-width:2px,color:#ffffff,font-weight:bold;
    
    class JwtConfig,JwtService,AuthService,UserService,TokenService,BettingService,MatchSim,OddsService,LeagueService,LeagueInit,ProfileService,Persist,E_User,E_Bet,E_Match,E_Team,E_Settings,E_Token,HbmXml,AuthRepo,RefreshRepo,Constants,CookieUtils,GenerateHash,BasicResp,LoginResp,ProfileResp,BettingResp,LeagueResp,MatchSimResp,OddsResp files;
    class Sec_Title,Auth_Title,Sport_Title,Store_Title,Infra_Title,Dto_Title titles;
