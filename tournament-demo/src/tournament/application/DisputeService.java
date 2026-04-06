package tournament.application;
import java.time.LocalDateTime; import java.util.*; import tournament.domain.*; import tournament.infrastructure.auth.AuthAdapter; import tournament.infrastructure.repository.TournamentRepository;
public class DisputeService {
 private final TournamentRepository repo; private final AuthAdapter auth; private final AuditService audit;
 public DisputeService(TournamentRepository repo,AuthAdapter auth,AuditService audit){this.repo=repo;this.auth=auth;this.audit=audit;}
 public void openDispute(String userId,int matchId,String reason){ if(auth.getRoleForUser(userId)==Role.ORGANIZER || auth.getRoleForUser(userId)==Role.REFEREE) throw new SecurityException("Participants/Captains only"); Tournament t=repo.getCurrentTournament(); Match m=t.findMatchById(matchId); if(m==null || m.getStatus()!=MatchStatus.FINALIZED) throw new IllegalStateException("Match must be finalized"); if(!t.getDisputeWindowPolicy().isWithinWindow(m.getFinalizedAt(), LocalDateTime.now())) throw new IllegalStateException("DisputeWindowClosed"); t.addDispute(new Dispute(matchId,reason)); audit.record("OpenDispute","Match "+matchId+": "+reason); }
 public List<Dispute> getDisputes(){ return repo.getCurrentTournament().getDisputes(); }
}
