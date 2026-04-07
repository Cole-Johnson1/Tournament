package tournament.presentation;

import java.util.*;
import tournament.application.*;
import tournament.domain.*;
import tournament.domain.factory.TournamentFactory;
import tournament.domain.policy.*;
import tournament.domain.strategy.*;

public class DemoRunner {
    private final TournamentFacade facade;
    private final TournamentConsoleUI ui;

    public DemoRunner(TournamentFacade facade, TournamentConsoleUI ui) {
        this.facade = facade;
        this.ui = ui;
    }

    public void runFullDemo() {
        printArchitectureSummary();
        printPatternMap();

        demoTournamentLifecycle();
        demoStateTransitions();
        demoQueries();
        demoPolicyComparison();
        demoSaveAndReload();
        demoErrorHandling();
        demoPerformanceBenchmark();

        section("AUDIT LOG (REQ-FR-011)");
        narrate("Every action above was recorded in the append-only audit log:");
        ui.showAuditLog();

        section("DEMO COMPLETE");
    }

    // ================================================================
    //  ARCHITECTURE SUMMARY
    // ================================================================
    private void printArchitectureSummary() {
        section("ARCHITECTURE SUMMARY");
        narrate("Layered Architecture (dependency direction: Infra -> App -> Domain)");
        narrate("");
        narrate("  Presentation Layer   tournament.presentation");
        narrate("    Main               Entry point (--demo flag for scripted run)");
        narrate("    TournamentConsoleUI Interactive CLI with sub-menus");
        narrate("    DemoRunner          Scripted demonstration (this output)");
        narrate("");
        narrate("  Application Layer    tournament.application");
        narrate("    TournamentFacade    Facade - single entry point for all operations");
        narrate("    TournamentService   Use-case orchestration + authorization");
        narrate("    MatchService        Match result submission");
        narrate("    StandingsService    Standings calculation");
        narrate("    DisputeService      Dispute open/resolve workflow");
        narrate("    AuditService        Append-only audit logging");
        narrate("");
        narrate("  Application Layer    tournament.application.repository  (INTERFACES)");
        narrate("    TournamentRepository  Interface - defines persistence contract");
        narrate("    AuditRepository       Interface - defines audit persistence contract");
        narrate("    (Domain depends on nothing; App depends on Domain only;");
        narrate("     Infrastructure implements App interfaces -> Dependency Inversion)");
        narrate("");
        narrate("  Domain Layer         tournament.domain");
        narrate("    Tournament, Team, Player, Match, MatchResult, Dispute, StandingsEntry");
        narrate("    domain/state/      State pattern - 8 concrete states");
        narrate("    domain/strategy/   Strategy pattern - scheduling algorithms");
        narrate("    domain/policy/     Configurable policies (roster, dispute window, rules)");
        narrate("    domain/factory/    Singleton + Simple Factory for tournament creation");
        narrate("");
        narrate("  Infrastructure Layer tournament.infrastructure");
        narrate("    repository/        InMemoryTournamentRepository, InMemoryAuditRepository");
        narrate("    auth/              AuthAdapter (Adapter pattern) + ExternalAuthProvider");
        narrate("    time/              SystemClockProvider");
    }

    // ================================================================
    //  PATTERN MAP
    // ================================================================
    private void printPatternMap() {
        section("DESIGN PATTERN MAP");
        narrate("Pattern              | Category    | Classes");
        narrate("---------------------|-------------|-----------------------------------------------");
        narrate("State                | Behavioral  | TournamentState (interface)");
        narrate("                     |             | AbstractTournamentState, DraftState,");
        narrate("                     |             | RegistrationOpenState, SeededState,");
        narrate("                     |             | ScheduledState, InProgressState,");
        narrate("                     |             | CompleteState, CancelledState");
        narrate("                     | Motivation  | Eliminates conditional logic for lifecycle;");
        narrate("                     |             | each state decides what transitions are legal.");
        narrate("---------------------|-------------|-----------------------------------------------");
        narrate("Strategy             | Behavioral  | SchedulerStrategy (interface)");
        narrate("                     |             | RoundRobinScheduler, EliminationScheduler");
        narrate("                     | Motivation  | Swap scheduling algorithm at runtime without");
        narrate("                     |             | changing Tournament class.");
        narrate("---------------------|-------------|-----------------------------------------------");
        narrate("Adapter              | Structural  | AuthAdapter (adapter)");
        narrate("                     |             | ExternalAuthProvider (target interface)");
        narrate("                     |             | SimpleExternalAuthProvider (adaptee)");
        narrate("                     | Motivation  | Maps external auth strings to internal Role");
        narrate("                     |             | enum; system is decoupled from auth provider.");
        narrate("---------------------|-------------|-----------------------------------------------");
        narrate("Facade               | Structural  | TournamentFacade");
        narrate("                     | Motivation  | Provides single entry point for 5 services;");
        narrate("                     |             | simplifies client code (UI only knows Facade).");
        narrate("---------------------|-------------|-----------------------------------------------");
        narrate("Singleton            | Creational  | TournamentFactory (private ctor, getInstance)");
        narrate("                     | Motivation  | One factory instance ensures consistent config;");
        narrate("                     |             | global access point avoids passing it around.");
        narrate("---------------------|-------------|-----------------------------------------------");
        narrate("Simple Factory       | Creational  | TournamentFactory.createTournament()");
        narrate("                     | Motivation  | Encapsulates which policies + strategy to wire");
        narrate("                     |             | per tournament type; caller doesn't know details.");
    }

    // ================================================================
    //  FULL TOURNAMENT LIFECYCLE
    // ================================================================
    private void demoTournamentLifecycle() {
        section("TOURNAMENT LIFECYCLE");
        narrate("Creating Round Robin tournament via Factory (Singleton)...");
        facade.createTournament("organizer1", "Campus Showdown", TournamentType.ROUND_ROBIN);
        narrate("State: " + state());

        facade.openRegistration("organizer1");
        narrate("Opened registration. State: " + state());

        facade.registerTeam("captain1", "Falcons", "Alice");
        facade.registerTeam("captain2", "Wolves", "Bob");
        facade.registerTeam("captain3", "Titans", "Carol");
        facade.registerTeam("captain4", "Sharks", "Dave");
        narrate("Registered 4 teams.");

        facade.addPlayerToTeam("captain1", "Falcons", "Alex");
        facade.addPlayerToTeam("captain1", "Falcons", "Amy");
        facade.addPlayerToTeam("captain2", "Wolves", "Brian");
        facade.addPlayerToTeam("captain2", "Wolves", "Beth");
        facade.addPlayerToTeam("captain3", "Titans", "Carlos");
        facade.addPlayerToTeam("captain3", "Titans", "Chloe");
        facade.addPlayerToTeam("captain4", "Sharks", "Dan");
        facade.addPlayerToTeam("captain4", "Sharks", "Diana");
        narrate("Added 2 players per team (RosterSizePolicy max=5).");

        facade.closeRegistrationAndSeed("organizer1");
        narrate("Closed registration. State: " + state());

        facade.generateSchedule("organizer1");
        narrate("Generated schedule. State: " + state());
        narrate(facade.getCurrentTournament().getMatches().size() + " matches created.");

        facade.startTournament("organizer1");
        narrate("Tournament started. State: " + state());

        ui.showTournamentSummary(facade.getCurrentTournament());

        narrate("Submitting all match results...");
        facade.submitResult("ref1", 1, 2, 1);
        facade.submitResult("ref1", 2, 0, 0);
        facade.submitResult("ref1", 3, 3, 1);
        facade.submitResult("ref1", 4, 1, 2);
        facade.submitResult("ref1", 5, 1, 0);
        facade.submitResult("ref1", 6, 2, 2);
        ui.showStandings(facade.getStandings());

        narrate("Opening dispute on Match #1...");
        facade.openDispute("captain1", 1, "Score was entered incorrectly");
        ui.showDisputes(facade.getDisputes());

        narrate("Resolving dispute (accepted, correcting to 1:1)...");
        facade.resolveDispute("organizer1", 1, true, 1, 1);
        narrate("Updated standings after dispute resolution:");
        ui.showStandings(facade.getStandings());

        facade.finalizeTournament("organizer1");
        narrate("Tournament finalized. State: " + state());
    }

    // ================================================================
    //  STATE TRANSITIONS (before/after + invalid)
    // ================================================================
    private void demoStateTransitions() {
        section("STATE TRANSITIONS (valid + invalid)");

        narrate("--- Valid Transition ---");
        narrate("Creating a new tournament to demonstrate state transitions...");
        TournamentFactory factory = TournamentFactory.getInstance();
        Tournament t2 = factory.createTournament("State Demo", TournamentType.ELIMINATION);
        narrate("  State BEFORE: " + t2.getState().getName());
        t2.openRegistration();
        narrate("  Action:       openRegistration()");
        narrate("  State AFTER:  " + t2.getState().getName());

        narrate("");
        narrate("--- Invalid Transition ---");
        narrate("  Attempting generateSchedule() while in RegistrationOpen state...");
        try {
            t2.generateSchedule();
        } catch (Exception e) {
            narrate("  REJECTED: " + e.getMessage());
        }
        narrate("  State remains: " + t2.getState().getName());
    }

    // ================================================================
    //  QUERIES (filter by status, search by team)
    // ================================================================
    private void demoQueries() {
        section("QUERY DEMONSTRATIONS");
        Tournament t = facade.getCurrentTournament();

        narrate("Query 1: Filter disputes by status = RESOLVED");
        List<Dispute> resolved = t.getDisputesByStatus(DisputeStatus.RESOLVED);
        if (resolved.isEmpty()) { narrate("  (none)"); }
        for (Dispute d : resolved) {
            narrate("  Match #" + d.getMatchId() + " | " + d.getStatus() + " | " + d.getReason());
        }

        narrate("");
        narrate("Query 2: Search all matches involving team 'Falcons'");
        List<Match> falconMatches = t.getMatchesByTeam("Falcons");
        for (Match m : falconMatches) {
            String score = m.getResult() != null
                    ? m.getResult().getTeamOneScore() + "-" + m.getResult().getTeamTwoScore()
                    : "n/a";
            narrate("  #" + m.getId() + " " + m.getTeamOne().getName()
                    + " vs " + m.getTeamTwo().getName() + " [" + m.getStatus() + "] " + score);
        }
    }

    // ================================================================
    //  POLICY COMPARISON (Policy A vs B)
    // ================================================================
    private void demoPolicyComparison() {
        section("POLICY COMPARISON (RuleSetPolicy A vs B)");

        narrate("Same match results, two different scoring policies:");
        narrate("");
        narrate("Policy A: StandardRuleSetPolicy  (Win=3, Draw=1)");
        narrate("Policy B: WinHeavyRuleSetPolicy  (Win=5, Draw=0)");
        narrate("");

        TournamentFactory factory = TournamentFactory.getInstance();

        // Policy A
        Tournament tA = factory.createTournamentWithPolicy("Policy-A-Test",
                TournamentType.ROUND_ROBIN, new StandardRuleSetPolicy());
        // Policy B
        Tournament tB = factory.createTournamentWithPolicy("Policy-B-Test",
                TournamentType.ROUND_ROBIN, new WinHeavyRuleSetPolicy());

        // Register same teams + play same results on both
        setupPolicyTournament(tA);
        setupPolicyTournament(tB);

        narrate("Results with Policy A (StandardRuleSetPolicy: Win=3, Draw=1):");
        printStandingsInline(tA.calculateStandings());

        narrate("Results with Policy B (WinHeavyRuleSetPolicy: Win=5, Draw=0):");
        printStandingsInline(tB.calculateStandings());

        narrate("Explanation: With Policy A, draws are worth 1 point, so a team that");
        narrate("draws frequently stays competitive. With Policy B, draws are worth 0,");
        narrate("so only outright wins matter - changing the ranking order.");
    }

    private void setupPolicyTournament(Tournament t) {
        t.openRegistration();
        t.registerTeam(new Team("Alpha", "Cap-A"));
        t.registerTeam(new Team("Beta", "Cap-B"));
        t.registerTeam(new Team("Gamma", "Cap-G"));
        t.closeRegistrationAndSeed();
        t.generateSchedule();
        t.startTournament();
        // Alpha vs Beta: Alpha wins
        t.findMatchById(1).finalizeResult(new MatchResult(2, 0), false);
        // Alpha vs Gamma: Draw
        t.findMatchById(2).finalizeResult(new MatchResult(1, 1), false);
        // Beta vs Gamma: Draw
        t.findMatchById(3).finalizeResult(new MatchResult(1, 1), false);
    }

    private void printStandingsInline(List<StandingsEntry> standings) {
        narrate("    Rank Team             W   D   L   Pts");
        narrate("    -----------------------------------");
        int rank = 1;
        for (StandingsEntry s : standings) {
            narrate(String.format("    %-4d %-14s %3d %3d %3d %5d",
                    rank++, s.getTeamName(), s.getWins(), s.getDraws(), s.getLosses(), s.getPoints()));
        }
    }

    // ================================================================
    //  SAVE AND RELOAD
    // ================================================================
    private void demoSaveAndReload() {
        section("SAVE AND RELOAD (Repository Boundary)");
        narrate("The repository interface is defined in tournament.application.repository");
        narrate("The implementation lives in tournament.infrastructure.repository");
        narrate("");
        narrate("Interface:      TournamentRepository (application layer)");
        narrate("Implementation: InMemoryTournamentRepository (infrastructure layer)");
        narrate("This allows swapping to a database or file-based repo without");
        narrate("changing any application or domain code.");
        narrate("");
        narrate("Demonstrating save and reload by name...");
        Tournament saved = facade.getCurrentTournament();
        narrate("  Saved tournament: '" + saved.getName() + "' (state=" + saved.getState().getName() + ")");

        narrate("  Creating a second tournament...");
        facade.createTournament("organizer1", "Winter Cup", TournamentType.ELIMINATION);
        narrate("  Current tournament is now: '" + facade.getCurrentTournament().getName() + "'");

        narrate("  Reloading 'Campus Showdown' from repository...");
        // Access through the facade's underlying service - we demonstrate the boundary
        Tournament reloaded = facade.findTournamentByName("Campus Showdown");
        if (reloaded != null) {
            narrate("  Reloaded: '" + reloaded.getName() + "' (state=" + reloaded.getState().getName()
                    + ", teams=" + reloaded.getTeams().size() + ")");
            narrate("  All data intact - repository boundary works correctly.");
        } else {
            narrate("  ERROR: Could not reload tournament.");
        }
    }

    // ================================================================
    //  ERROR HANDLING
    // ================================================================
    private void demoErrorHandling() {
        section("ERROR HANDLING (2+ invalid operations)");

        narrate("1. Attempting to register team on completed tournament...");
        safeRun(() -> facade.registerTeam("captain1", "Ghost Team", "Nobody"));

        narrate("2. Attempting unauthorized action (participant tries organizer action)...");
        safeRun(() -> facade.openRegistration("participant1"));

        narrate("Both errors produce predictable, descriptive messages.");
    }

    // ================================================================
    //  PERFORMANCE BENCHMARK
    // ================================================================
    private void demoPerformanceBenchmark() {
        section("PERFORMANCE BENCHMARK (Hot-Path)");
        narrate("Hot path: submitResult -> standings recalculation");
        narrate("Scenario: Round Robin tournament with 32 teams = 496 matches");
        narrate("");

        TournamentFactory factory = TournamentFactory.getInstance();
        Tournament perf = factory.createTournament("Perf-Test", TournamentType.ROUND_ROBIN);
        perf.openRegistration();
        int teamCount = 32;
        for (int i = 1; i <= teamCount; i++) {
            perf.registerTeam(new Team("Team-" + i, "Cap-" + i));
        }
        perf.closeRegistrationAndSeed();
        perf.generateSchedule();
        perf.startTournament();

        int matchCount = perf.getMatches().size();
        narrate("Teams: " + teamCount + "  |  Matches generated: " + matchCount);

        // Submit all results and time it
        long startSubmit = System.nanoTime();
        Random rng = new Random(42);
        for (Match m : perf.getMatches()) {
            m.finalizeResult(new MatchResult(rng.nextInt(5), rng.nextInt(5)), false);
        }
        long endSubmit = System.nanoTime();

        // Time standings calculation
        long startStandings = System.nanoTime();
        List<StandingsEntry> standings = perf.calculateStandings();
        long endStandings = System.nanoTime();

        double submitMs = (endSubmit - startSubmit) / 1_000_000.0;
        double standingsMs = (endStandings - startStandings) / 1_000_000.0;
        double totalMs = submitMs + standingsMs;

        narrate(String.format("  Submit %d results:        %.2f ms", matchCount, submitMs));
        narrate(String.format("  Calculate standings:      %.2f ms", standingsMs));
        narrate(String.format("  Total hot-path time:      %.2f ms", totalMs));
        narrate("");
        narrate("Trade-off: Standings are recomputed from scratch on each call");
        narrate("(full recompute strategy) to guarantee determinism (INV-05).");
        narrate("This is reliable but O(matches). An incremental approach would");
        narrate("be faster but risks inconsistency if a result is corrected.");
        narrate(String.format("At %d teams the p95 target is <200ms. Measured: %.2f ms.", teamCount, totalMs));
    }

    // ================================================================
    //  HELPERS
    // ================================================================
    private String state() {
        return facade.getCurrentTournament().getState().getName();
    }

    private void section(String title) {
        System.out.println();
        ui.printLine();
        System.out.println("  >> " + title);
        ui.printLine();
    }

    private void narrate(String msg) {
        System.out.println("  " + msg);
    }

    private void safeRun(Runnable action) {
        try { action.run(); }
        catch (Exception e) { narrate("  -> Caught: " + e.getMessage()); }
    }
}
