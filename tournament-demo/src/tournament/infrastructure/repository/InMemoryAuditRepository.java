package tournament.infrastructure.repository;
import java.util.*; import tournament.domain.AuditEvent;
public class InMemoryAuditRepository implements AuditRepository { private final List<AuditEvent> events=new ArrayList<>(); public void add(AuditEvent e){events.add(e);} public List<AuditEvent> findAll(){return Collections.unmodifiableList(events);} }
