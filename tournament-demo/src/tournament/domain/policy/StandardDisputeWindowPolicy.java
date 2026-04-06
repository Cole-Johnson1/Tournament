package tournament.domain.policy;
import java.time.LocalDateTime;
public class StandardDisputeWindowPolicy implements DisputeWindowPolicy {
 private final long hours; public StandardDisputeWindowPolicy(long hours){this.hours=hours;}
 public boolean isWithinWindow(LocalDateTime finalizedAt, LocalDateTime openedAt){ return finalizedAt!=null && !openedAt.isAfter(finalizedAt.plusHours(hours)); }
}
