package tournament.domain;
import java.time.LocalDateTime;
public class Match {
 private final int id; private final Team teamOne, teamTwo; private MatchStatus status=MatchStatus.SCHEDULED; private MatchResult result; private LocalDateTime finalizedAt;
 public Match(int id,Team teamOne,Team teamTwo){ if(teamOne.getName().equalsIgnoreCase(teamTwo.getName())) throw new IllegalArgumentException("Teams must be distinct"); this.id=id; this.teamOne=teamOne; this.teamTwo=teamTwo; }
 public void start(){ if(status==MatchStatus.SCHEDULED) status=MatchStatus.IN_PROGRESS; }
 public void finalizeResult(MatchResult result, boolean overrideAllowed){ if(status==MatchStatus.FINALIZED && !overrideAllowed) throw new IllegalStateException("Result already finalized"); if(status!=MatchStatus.IN_PROGRESS && !(status==MatchStatus.FINALIZED && overrideAllowed)) throw new IllegalStateException("MatchNotReportable"); this.result=result; this.status=MatchStatus.FINALIZED; this.finalizedAt=LocalDateTime.now(); }
 public int getId(){return id;} public Team getTeamOne(){return teamOne;} public Team getTeamTwo(){return teamTwo;} public MatchStatus getStatus(){return status;} public MatchResult getResult(){return result;} public LocalDateTime getFinalizedAt(){return finalizedAt;}
}
