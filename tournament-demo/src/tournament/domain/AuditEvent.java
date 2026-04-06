package tournament.domain;
import java.time.LocalDateTime;
public class AuditEvent {
 private final LocalDateTime timestamp=LocalDateTime.now(); private final String action,details;
 public AuditEvent(String action,String details){this.action=action;this.details=details;}
 public String toString(){ return timestamp+" | "+action+" | "+details; }
}
