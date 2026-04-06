package tournament.domain;
import java.time.LocalDateTime;
public class Dispute {
 private final int matchId; private final String reason; private final LocalDateTime openedAt; private DisputeStatus status=DisputeStatus.OPEN;
 public Dispute(int matchId,String reason){this.matchId=matchId;this.reason=reason;this.openedAt=LocalDateTime.now();}
 public void resolve(){ if(status!=DisputeStatus.OPEN) throw new IllegalStateException("Dispute is not open"); status=DisputeStatus.RESOLVED; }
 public void reject(){ if(status!=DisputeStatus.OPEN) throw new IllegalStateException("Dispute is not open"); status=DisputeStatus.REJECTED; }
 public int getMatchId(){return matchId;} public String getReason(){return reason;} public LocalDateTime getOpenedAt(){return openedAt;} public DisputeStatus getStatus(){return status;}
}
