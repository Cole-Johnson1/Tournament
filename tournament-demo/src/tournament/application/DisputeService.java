package tournament.application;
import java.time.LocalDateTime;
 import java.util.*;
 import tournament.application.repository.TournamentRepository;
 import tournament.domain.*;
 import tournament.infrastructure.auth.AuthAdapter;
public class DisputeService {
 private final TournamentRepository repo; private final AuthAdapter auth; private final AuditService audit;
 public DisputeService(TournamentRepository repo,AuthAdapter auth,AuditService audit){this.repo=repo;this.auth=auth;this.audit=audit;}
 public void openDispute(String userId,int matchId,String reason){ if(auth.getRoleForUser(userId)==Role.ORGANIZER || auth.getRoleForUser(userId)==Role.REFEREE) throw new SecurityException("Participants/Captains only"); Tournament t=repo.getCurrentTournament(); Match m=t.findMatchById(matchId); if(m==null || m.getStatus()!=MatchStatus.FINALIZED) throw new IllegalStateException("Match must be finalized"); if(!t.getDisputeWindowPolicy().isWithinWindow(m.getFinalizedAt(), LocalDateTime.now())) throw new IllegalStateException("DisputeWindowClosed"); t.addDispute(new Dispute(matchId,reason)); audit.record("OpenDispute","Match "+matchId+": "+reason); }

 public void resolveDispute(String userId,int matchId,boolean accepted,int newScoreA,int newScoreB){ if(auth.getRoleForUser(userId)!=Role.ORGANIZER) throw new SecurityException("AuthorizationFailed"); Tournament t=repo.getCurrentTournament(); Dispute dispute=null; for(Dispute d:t.getDisputes()) if(d.getMatchId()==matchId && d.getStatus()==DisputeStatus.OPEN){ dispute=d; break; } if(dispute==null) throw new IllegalStateException("No open dispute for match "+matchId); if(accepted){ dispute.resolve(); Match m=t.findMatchById(matchId); m.finalizeResult(new MatchResult(newScoreA,newScoreB),true); audit.record("ResolveDispute","Match "+matchId+" accepted => "+newScoreA+":"+newScoreB); } else { dispute.reject(); audit.record("ResolveDispute","Match "+matchId+" rejected"); } }

 public List<Dispute> getDisputes(){ return repo.getCurrentTournament().getDisputes(); }
}
