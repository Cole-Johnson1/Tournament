package tournament.presentation;

import java.util.*;
import tournament.application.*;
import tournament.domain.*;
import tournament.domain.factory.TournamentFactory;
import tournament.domain.policy.*;

public class DemoRunner {
    private final TournamentFacade facade;
    private final TournamentConsoleUI ui;

    public DemoRunner(TournamentFacade facade, TournamentConsoleUI ui) {
        this.facade = facade;
        this.ui = ui;
    }

    public void runFullDemo() {
        printArchitectureSummary();

        demoTournamentLifecycle();
        demoStateTransitions();
        demoQueries();
        demoPolicyComparison();
        demoSaveAndReload();
        demoErrorHandling();
        demoPerformanceBenchmark();

        section("AUDIT LOG");
        ui.showAuditLog();

        section("DEMO COMPLETE");
    }

    // ================================================================
    //  ARCHITECTURE SUMMARY
    // ================================================================
    private void printArchitectureSummary() {
        section("ARCHITECTURE SUMMARY");
        narrate("Layered Architecture (Infra -> App -> Domain)");
        narrate("");
        narrate("  Presentation   Main, TournamentConsoleUI, DemoRunner");
        narrate("  Application    TournamentFacade, TournamentService, MatchService,");
        narrate("                 StandingsService, DisputeService, AuditService");
        narrate("  App/Repository TournamentRepository, AuditRepository (interfaces)");
        narrate("  Domain         Tournament, Team, Player, Match, Dispute, StandingsEntry");
        narrate("    state/       DraftState .. CompleteState, CancelledState (8 states)");
        narrate("    strategy/    RoundRobinScheduler, EliminationScheduler");
        narrate("    policy/      RuleSetPolicy, RosterSizePolicy, DisputeWindowPolicy");
        narrate("    factory/     TournamentFactory (Singleton + Simple Factory)");
        narrate("  Infrastructure InMemoryTournamentRepository, InMemoryAuditRepository,");
        narrate("                 AuthAdapter, SystemClockProvider");
    }



    // ================================================================
    //  FULL TOURNAMENT LIFECYCLE
    // ================================================================
    private void demoTournamentLifecycle() {
        section("TOURNAMENT LIFECYCLE");
        facade.createTournament("organizer1", "Campus Showdown", TournamentType.ROUND_ROBIN);
        narrate("Created tournament. State: " + state());

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
        narrate("Added players to teams.");

        facade.closeRegistrationAndSeed("organizer1");
        narrate("Closed registration. State: " + state());

        facade.generateSchedule("organizer1");
        narrate("Generated schedule. State: " + state());
        narrate(facade.getCurrentTournament().getMatches().size() + " matches created.");

        facade.startTournament("organizer1");
        narrate("Started. State: " + state());

        ui.showTournamentSummary(facade.getCurrentTournament());

        narrate("Submitting match results...");
        facade.submitResult("ref1", 1, 2, 1);
        facade.submitResult("ref1", 2, 0, 0);
        facade.submitResult("ref1", 3, 3, 1);
        facade.submitResult("ref1", 4, 1, 2);
        facade.submitResult("ref1", 5, 1, 0);
        facade.submitResult("ref1", 6, 2, 2);
        ui.showStandings(facade.getStandings());

        facade.openDispute("captain1", 1, "Score was entered incorrectly");
        narrate("Opened dispute on Match #1.");
        ui.showDisputes(facade.getDisputes());

        facade.resolveDispute("organizer1", 1, true, 1, 1);
        narrate("Resolved dispute, corrected score to 1:1. Updated standings:");
        ui.showStandings(facade.getStandings());

        facade.finalizeTournament("organizer1");
        narrate("Finalized. State: " + state());
    }

    // ================================================================
    //  STATE TRANSITIONS (before/after + invalid)
    // ================================================================
    private void demoStateTransitions() {
        section("STATE TRANSITIONS");

        TournamentFactory factory = TournamentFactory.getInstance();
        Tournament t2 = factory.createTournament("State Demo", TournamentType.ELIMINATION);

        narrate("Valid: Draft -> openRegistration()");
        narrate("  Before: " + t2.getState().getName());
        t2.openRegistration();
        narrate("  After:  " + t2.getState().getName());

        narrate("");
        narrate("Invalid: RegistrationOpen -> generateSchedule()");
        try {
            t2.generateSchedule();
        } catch (Exception e) {
            narrate("  Rejected: " + e.getMessage());
        }
        narrate("  State unchanged: " + t2.getState().getName());
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
        section("POLICY COMPARISON");

        narrate("Policy A: StandardRuleSetPolicy (Win=3, Draw=1)");
        narrate("Policy B: WinHeavyRuleSetPolicy (Win=5, Draw=0)");
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
        section("SAVE AND RELOAD");
        Tournament saved = facade.getCurrentTournament();
        narrate("Saved: '" + saved.getName() + "' (state=" + saved.getState().getName() + ")");

        facade.createTournament("organizer1", "Winter Cup", TournamentType.ELIMINATION);
        narrate("Switched to: '" + facade.getCurrentTournament().getName() + "'");

        Tournament reloaded = facade.findTournamentByName("Campus Showdown");
        if (reloaded != null) {
            narrate("Reloaded: '" + reloaded.getName() + "' (state=" + reloaded.getState().getName()
                    + ", teams=" + reloaded.getTeams().size() + ")");
        } else {
            narrate("ERROR: Could not reload tournament.");
        }
    }

    // ================================================================
    //  ERROR HANDLING
    // ================================================================
    private void demoErrorHandling() {
        section("ERROR HANDLING");

        narrate("1. Register team on completed tournament:");
        safeRun(() -> facade.registerTeam("captain1", "Ghost Team", "Nobody"));

        narrate("2. Unauthorized action (participant tries organizer action):");
        safeRun(() -> facade.openRegistration("participant1"));
    }

    // ================================================================
    //  PERFORMANCE BENCHMARK
    // ================================================================
    private void demoPerformanceBenchmark() {
        section("PERFORMANCE BENCHMARK");
        narrate("Round Robin, 32 teams, 496 matches");

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
