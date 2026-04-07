# Esports Tournament Manager

CSCN72040 - Software Design Techniques, Phase 2

## Team

- Ishaq Ishaq Nasiru
- Cole Johnson
- Shumroz Usmani

## Prerequisites

- Java 17 or later (uses `switch` expressions, pattern matching)
- No external dependencies - plain `javac` and `java`

## Build

```bash
cd tournament-demo
mkdir -p bin
javac -d bin $(find src -name "*.java")
```

**Windows (PowerShell):**

```powershell
cd tournament-demo
mkdir bin -Force
javac -d bin (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object FullName)
```

## Run

### Scripted Demo (Phase 2 deliverable)

```bash
java -cp bin tournament.presentation.Main --demo
```

This runs the full scripted demonstration covering all Phase 2 requirements:
architecture summary, pattern map, lifecycle walkthrough, state transitions,
queries, policy comparison, save/reload, error handling, and performance benchmark.

### Interactive CLI

```bash
java -cp bin tournament.presentation.Main
```

Opens the menu-driven interface with sub-menus for Setup, Run, and Review.

## Project Structure

```
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

## Design Patterns

| Pattern        | Category   | Key Classes                                          |
|----------------|------------|------------------------------------------------------|
| State          | Behavioral | TournamentState, DraftState, ..., CancelledState     |
| Strategy       | Behavioral | SchedulerStrategy, RoundRobinScheduler, EliminationScheduler |
| Adapter        | Structural | AuthAdapter, ExternalAuthProvider                     |
| Facade         | Structural | TournamentFacade                                     |
| Singleton      | Creational | TournamentFactory                                    |
| Simple Factory | Creational | TournamentFactory.createTournament()                 |

## Configuration

- **Roster size:** `FixedRosterSizePolicy(5)` - set in TournamentFactory
- **Dispute window:** `StandardDisputeWindowPolicy(48)` - 48 hours
- **Scoring rules:** `StandardRuleSetPolicy` (Win=3, Draw=1) or `WinHeavyRuleSetPolicy` (Win=5, Draw=0)
- **Scheduling:** `RoundRobinScheduler` or `EliminationScheduler` - selected by tournament type
- All configuration is in `TournamentFactory.java` - no hardcoded absolute paths
