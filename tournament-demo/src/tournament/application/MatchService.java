package tournament.application;
import tournament.application.repository.TournamentRepository;
 import tournament.domain.*;
 import tournament.infrastructure.auth.AuthAdapter;
public class MatchService { private final TournamentRepository repo; private final AuthAdapter auth; private final AuditService audit; public MatchService(TournamentRepository repo,AuthAdapter auth,AuditService audit){this.repo=repo;this.auth=auth;this.audit=audit;} public void submitResult(String userId,int matchId,int a,int b){ if(auth.getRoleForUser(userId)!=Role.REFEREE) throw new SecurityException("AuthorizationFailed"); Match m=repo.getCurrentTournament().findMatchById(matchId); if(m==null) throw new IllegalArgumentException("Match not found"); m.finalizeResult(new MatchResult(a,b),false); audit.record("SubmitResult","Match "+matchId+" => "+a+":"+b); } }
