package tournament.application;

import java.util.*;
import tournament.domain.*;

public class TournamentFacade {
    private final TournamentService tournamentService;
    private final MatchService matchService;
    private final StandingsService standingsService;
    private final DisputeService disputeService;
    private final AuditService auditService;

    public TournamentFacade(TournamentService tournamentService, MatchService matchService,
                            StandingsService standingsService, DisputeService disputeService,
                            AuditService auditService) {
        this.tournamentService = tournamentService;
        this.matchService = matchService;
        this.standingsService = standingsService;
        this.disputeService = disputeService;
        this.auditService = auditService;
    }

    public Tournament createTournament(String userId, String name, TournamentType type) {
        return tournamentService.createTournament(userId, name, type);
    }

    public void openRegistration(String userId) {
        tournamentService.openRegistration(userId);
    }

    public void registerTeam(String userId, String teamName, String captainName) {
        tournamentService.registerTeam(userId, teamName, captainName);
    }

    public void addPlayerToTeam(String userId, String teamName, String playerName) {
        tournamentService.addPlayerToTeam(userId, teamName, playerName);
    }

    public void closeRegistrationAndSeed(String userId) {
        tournamentService.closeRegistrationAndSeed(userId);
    }

    public void generateSchedule(String userId) {
        tournamentService.generateSchedule(userId);
    }

    public void startTournament(String userId) {
        tournamentService.startTournament(userId);
    }

    public void cancelTournament(String userId) {
        tournamentService.cancelTournament(userId);
    }

    public void submitResult(String userId, int matchId, int scoreA, int scoreB) {
        matchService.submitResult(userId, matchId, scoreA, scoreB);
    }

    public void finalizeTournament(String userId) {
        tournamentService.finalizeTournament(userId);
    }

    public List<StandingsEntry> getStandings() {
        return standingsService.getStandings();
    }

    public void openDispute(String userId, int matchId, String reason) {
        disputeService.openDispute(userId, matchId, reason);
    }

    public void resolveDispute(String userId, int matchId, boolean accepted, int newScoreA, int newScoreB) {
        disputeService.resolveDispute(userId, matchId, accepted, newScoreA, newScoreB);
    }

    public List<Dispute> getDisputes() {
        return disputeService.getDisputes();
    }

    public Tournament getCurrentTournament() {
        return tournamentService.getCurrentTournament();
    }

    public List<AuditEvent> getAuditLog() {
        return auditService.getAuditLog();
    }
}
