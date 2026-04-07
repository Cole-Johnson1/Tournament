package tournament.presentation;

import tournament.application.*;
import tournament.application.repository.*;
import tournament.domain.factory.TournamentFactory;
import tournament.infrastructure.auth.*;
import tournament.infrastructure.repository.*;

public class Main {
    public static void main(String[] args) {
        TournamentRepository tournamentRepo = new InMemoryTournamentRepository();
        AuditRepository auditRepo = new InMemoryAuditRepository();
        AuthAdapter authAdapter = new AuthAdapter(new SimpleExternalAuthProvider());
        AuditService auditService = new AuditService(auditRepo);
        TournamentService tournamentService = new TournamentService(
                tournamentRepo, TournamentFactory.getInstance(), authAdapter, auditService);
        MatchService matchService = new MatchService(tournamentRepo, authAdapter, auditService);
        StandingsService standingsService = new StandingsService(tournamentRepo);
        DisputeService disputeService = new DisputeService(tournamentRepo, authAdapter, auditService);
        TournamentFacade facade = new TournamentFacade(
                tournamentService, matchService, standingsService, disputeService, auditService);

        TournamentConsoleUI ui = new TournamentConsoleUI(facade);

        if (args.length > 0 && "--demo".equals(args[0])) {
            new DemoRunner(facade, ui).runFullDemo();
        } else {
            ui.run();
        }
    }
}
