package tournament.infrastructure.repository;
import java.util.*; import tournament.domain.AuditEvent;
public interface AuditRepository { void add(AuditEvent e); List<AuditEvent> findAll(); }
