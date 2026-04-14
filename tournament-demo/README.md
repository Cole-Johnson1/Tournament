# Esports Tournament Manager

CSCN72040 - Software Design Techniques, Phase 2

## group
- Group 3
- Cole Johnson 


## Prerequisites

- Java 17 or later (uses `switch` expressions, pattern matching)
- No external dependencies - plain `javac` and `java`

## Build

```bash
cd tournament-demo
mkdir -p bin
javac -d bin $(find src -name "*.java")
```

```powershell
cd tournament-demo
mkdir bin -Force
javac -d bin (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object FullName)
```
## To Run

### Demo
```bash
java -cp bin tournament.presentation.Main --demo
```
This runs the full scripted demonstration covering all Phase 2 requirements:
architecture summary, lifecycle walkthrough, state transitions,
queries, policy comparison, save/reload, error handling, and performance benchmark

### Interactive CLI
```bash
java -cp bin tournament.presentation.Main
```
Opens the menu-driven interface with sub-menus for Setup, Run, and Review.


## Project Structure
src/tournament/
  presentation/         Entry point, CLI, demo runner
    Main.java           --demo flag or interactive menu
    TournamentConsoleUI  Menu-driven CLI with sub-menus
    DemoRunner           Scripted demo covering all requirements

  application/          Use-case orchestration
    TournamentFacade     Facade pattern - single API for all operations
    TournamentService    Tournament lifecycle + authorization
    MatchService         Match result submission
    StandingsService     Standings calculation
    DisputeService       Dispute open/resolve workflow
    AuditService         Append-only audit logging
    repository/          Repository INTERFACES (dependency inversion)
      TournamentRepository
      AuditRepository

  domain/               Core business logic (depends on nothing)
    Tournament, Team, Player, Match, MatchResult
    Dispute, DisputeStatus, StandingsEntry
    state/              State pattern (8 states)
    strategy/           Strategy pattern (RoundRobin, Elimination)
    policy/             Configurable policies
    factory/            Singleton + Simple Factory

  infrastructure/       External concerns
    repository/         InMemory implementations of App interfaces
    auth/               Adapter pattern (AuthAdapter)
    time/               Clock provider
```
