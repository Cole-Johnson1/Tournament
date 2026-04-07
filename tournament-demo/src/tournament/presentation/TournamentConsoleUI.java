package tournament.presentation;

import java.util.*;
import tournament.application.*;
import tournament.domain.*;

public class TournamentConsoleUI {
    private final TournamentFacade facade;
    private final Scanner sc = new Scanner(System.in);

    public TournamentConsoleUI(TournamentFacade facade) {
        this.facade = facade;
    }

    // ---- Entry Point ----
    public void run() {
        printBanner();
        while (true) {
            System.out.println("\n+========================================+");
            System.out.println("|            MAIN MENU                   |");
            System.out.println("+========================================+");
            System.out.println("|  1. Run Full Demo                      |");
            System.out.println("|  2. Tournament Management              |");
            System.out.println("|  3. View Audit Log                     |");
            System.out.println("|  0. Exit                               |");
            System.out.println("+========================================+");
            switch (prompt("Select")) {
                case "1" -> new DemoRunner(facade, this).runFullDemo();
                case "2" -> tournamentMenu();
                case "3" -> showAuditLog();
                case "0" -> { printLine(); System.out.println("Goodbye!"); return; }
                default -> System.out.println("  Invalid choice.");
            }
        }
    }

    // ---- Tournament Management Menu ----
    private void tournamentMenu() {
        while (true) {
            Tournament t = facade.getCurrentTournament();
            String state = t == null ? "No Tournament" : t.getState().getName();

            System.out.println("\n+----------------------------------------+");
            System.out.println("|       TOURNAMENT MANAGEMENT            |");
            System.out.println("|  Current State: " + pad(state, 23) + "|");
            System.out.println("+----------------------------------------+");
            System.out.println("|  1. Create Tournament                  |");
            System.out.println("|  2. Setup  (Registration / Roster)     |");
            System.out.println("|  3. Run    (Schedule / Matches)        |");
            System.out.println("|  4. Review (Standings / Disputes)      |");
            System.out.println("|  5. View Tournament Summary            |");
            System.out.println("|  6. Cancel Tournament                  |");
            System.out.println("|  0. Back                               |");
            System.out.println("+----------------------------------------+");
            switch (prompt("Select")) {
                case "1" -> createTournament();
                case "2" -> setupMenu();
                case "3" -> runMenu();
                case "4" -> reviewMenu();
                case "5" -> showTournamentSummary(facade.getCurrentTournament());
                case "6" -> safeRun(() -> { facade.cancelTournament("organizer1"); System.out.println("  Tournament cancelled."); });
                case "0" -> { return; }
                default -> System.out.println("  Invalid choice.");
            }
        }
    }

    // ---- Setup Sub-menu ----
    private void setupMenu() {
        while (true) {
            System.out.println("\n  +-- SETUP ----------------------------+");
            System.out.println("  |  1. Open Registration                |");
            System.out.println("  |  2. Register Team                    |");
            System.out.println("  |  3. Add Player to Team               |");
            System.out.println("  |  4. Close Registration & Seed        |");
            System.out.println("  |  0. Back                             |");
            System.out.println("  +--------------------------------------+");
            switch (prompt("  Select")) {
                case "1" -> safeRun(() -> { facade.openRegistration("organizer1"); System.out.println("  Registration opened."); });
                case "2" -> registerTeam();
                case "3" -> addPlayer();
                case "4" -> safeRun(() -> { facade.closeRegistrationAndSeed("organizer1"); System.out.println("  Registration closed and teams seeded."); });
                case "0" -> { return; }
                default -> System.out.println("  Invalid choice.");
            }
        }
    }

    // ---- Run Sub-menu ----
    private void runMenu() {
        while (true) {
            System.out.println("\n  +-- RUN ------------------------------+");
            System.out.println("  |  1. Generate Schedule                |");
            System.out.println("  |  2. Start Tournament                 |");
            System.out.println("  |  3. Submit Match Result              |");
            System.out.println("  |  4. Finalize Tournament              |");
            System.out.println("  |  5. View Matches                     |");
            System.out.println("  |  0. Back                             |");
            System.out.println("  +--------------------------------------+");
            switch (prompt("  Select")) {
                case "1" -> safeRun(() -> { facade.generateSchedule("organizer1"); System.out.println("  Schedule generated."); });
                case "2" -> safeRun(() -> { facade.startTournament("organizer1"); System.out.println("  Tournament started."); });
                case "3" -> submitResult();
                case "4" -> safeRun(() -> { facade.finalizeTournament("organizer1"); System.out.println("  Tournament finalized."); });
                case "5" -> showMatches();
                case "0" -> { return; }
                default -> System.out.println("  Invalid choice.");
            }
        }
    }

    // ---- Review Sub-menu ----
    private void reviewMenu() {
        while (true) {
            System.out.println("\n  +-- REVIEW ---------------------------+");
            System.out.println("  |  1. View Standings                   |");
            System.out.println("  |  2. Open Dispute                     |");
            System.out.println("  |  3. Resolve Dispute                  |");
            System.out.println("  |  4. View Disputes                    |");
            System.out.println("  |  0. Back                             |");
            System.out.println("  +--------------------------------------+");
            switch (prompt("  Select")) {
                case "1" -> safeRun(() -> showStandings(facade.getStandings()));
                case "2" -> openDispute();
                case "3" -> resolveDispute();
                case "4" -> safeRun(() -> showDisputes(facade.getDisputes()));
                case "0" -> { return; }
                default -> System.out.println("  Invalid choice.");
            }
        }
    }

    // ---- Input Workflows ----
    private void createTournament() {
        String name = prompt("  Tournament name");
        System.out.println("  1. Round Robin");
        System.out.println("  2. Elimination");
        String t = prompt("  Format");
        TournamentType type = "2".equals(t) ? TournamentType.ELIMINATION : TournamentType.ROUND_ROBIN;
        safeRun(() -> { facade.createTournament("organizer1", name, type); System.out.println("  Tournament '" + name + "' created."); });
    }

    private void registerTeam() {
        String team = prompt("  Team name");
        System.out.println("  Available captain IDs: captain1, captain2, captain3, captain4");
        String cid = prompt("  Captain ID");
        String cap = prompt("  Captain display name");
        safeRun(() -> { facade.registerTeam(cid, team, cap); System.out.println("  Team '" + team + "' registered."); });
    }

    private void addPlayer() {
        String cid = prompt("  Captain ID");
        String team = prompt("  Team name");
        String player = prompt("  Player name");
        safeRun(() -> { facade.addPlayerToTeam(cid, team, player); System.out.println("  Player '" + player + "' added to " + team + "."); });
    }

    private void submitResult() {
        showMatches();
        try {
            int id = Integer.parseInt(prompt("  Match ID"));
            int a = Integer.parseInt(prompt("  Score team 1"));
            int b = Integer.parseInt(prompt("  Score team 2"));
            safeRun(() -> { facade.submitResult("ref1", id, a, b); System.out.println("  Result recorded."); });
        } catch (NumberFormatException e) {
            System.out.println("  Invalid number.");
        }
    }

    private void openDispute() {
        try {
            int id = Integer.parseInt(prompt("  Match ID"));
            String reason = prompt("  Reason");
            safeRun(() -> { facade.openDispute("captain1", id, reason); System.out.println("  Dispute opened."); });
        } catch (NumberFormatException e) {
            System.out.println("  Invalid number.");
        }
    }

    private void resolveDispute() {
        safeRun(() -> showDisputes(facade.getDisputes()));
        try {
            int id = Integer.parseInt(prompt("  Match ID"));
            String yn = prompt("  Accept dispute? (y/n)");
            boolean accepted = "y".equalsIgnoreCase(yn.trim());
            int na = 0, nb = 0;
            if (accepted) {
                na = Integer.parseInt(prompt("  Corrected score team 1"));
                nb = Integer.parseInt(prompt("  Corrected score team 2"));
            }
            final int fa = na, fb = nb;
            safeRun(() -> { facade.resolveDispute("organizer1", id, accepted, fa, fb);
                System.out.println(accepted ? "  Dispute accepted, score corrected." : "  Dispute rejected."); });
        } catch (NumberFormatException e) {
            System.out.println("  Invalid number.");
        }
    }

    // ---- Display Helpers ----
    public void showTournamentSummary(Tournament t) {
        if (t == null) { System.out.println("  No tournament created yet."); return; }
        printLine();
        System.out.println("  TOURNAMENT: " + t.getName());
        System.out.println("  Format:     " + t.getType() + " (" + t.getSchedulerStrategy().getName() + ")");
        System.out.println("  State:      " + t.getState().getName());
        System.out.println("  Teams:      " + t.getTeams().size());
        printLine();
        for (Team team : t.getTeams()) {
            System.out.print("    " + team.getName() + " (captain: " + team.getCaptainName() + ") roster: [");
            StringJoiner sj = new StringJoiner(", ");
            for (Player p : team.getRoster()) sj.add(p.getName());
            System.out.println(sj + "]");
        }
        if (!t.getMatches().isEmpty()) {
            printLine();
            showMatches();
        }
    }

    private void showMatches() {
        Tournament t = facade.getCurrentTournament();
        if (t == null || t.getMatches().isEmpty()) { System.out.println("  No matches."); return; }
        System.out.println("  MATCHES:");
        for (Match m : t.getMatches()) {
            String score = m.getResult() != null
                    ? m.getResult().getTeamOneScore() + " - " + m.getResult().getTeamTwoScore()
                    : "  -  ";
            System.out.printf("    #%-2d  %-12s vs %-12s  [%-11s]  %s%n",
                    m.getId(), m.getTeamOne().getName(), m.getTeamTwo().getName(),
                    m.getStatus(), score);
        }
    }

    public void showStandings(List<StandingsEntry> standings) {
        printLine();
        System.out.println("  STANDINGS:");
        System.out.printf("    %-4s %-14s %3s %3s %3s %5s%n", "Rank", "Team", "W", "D", "L", "Pts");
        System.out.println("    " + "-".repeat(35));
        int rank = 1;
        for (StandingsEntry s : standings) {
            System.out.printf("    %-4d %-14s %3d %3d %3d %5d%n",
                    rank++, s.getTeamName(), s.getWins(), s.getDraws(), s.getLosses(), s.getPoints());
        }
        printLine();
    }

    public void showDisputes(List<Dispute> disputes) {
        if (disputes.isEmpty()) { System.out.println("  No disputes."); return; }
        System.out.println("  DISPUTES:");
        for (Dispute d : disputes) {
            System.out.printf("    Match #%-2d | %-10s | %s%n", d.getMatchId(), d.getStatus(), d.getReason());
        }
    }

    public void showAuditLog() {
        printLine();
        System.out.println("  AUDIT LOG:");
        for (AuditEvent e : facade.getAuditLog())
            System.out.println("    " + e);
        printLine();
    }

    // ---- Utilities ----
    private String prompt(String label) {
        System.out.print(label + ": ");
        return sc.nextLine().trim();
    }

    private void safeRun(Runnable action) {
        try { action.run(); }
        catch (Exception e) { System.out.println("  Error: " + e.getMessage()); }
    }

    void printLine() {
        System.out.println("  ----------------------------------------");
    }

    private void printBanner() {
        System.out.println();
        System.out.println("  +======================================+");
        System.out.println("  |   ESPORTS TOURNAMENT MANAGER v2.0    |");
        System.out.println("  |   CSCN72040 - Software Design        |");
        System.out.println("  +======================================+");
    }

    private String pad(String s, int width) {
        if (s.length() >= width) return s.substring(0, width);
        return s + " ".repeat(width - s.length());
    }
}
