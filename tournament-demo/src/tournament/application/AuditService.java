package tournament.application;
import java.util.*;
 import tournament.application.repository.AuditRepository;
 import tournament.domain.*;
public class AuditService { private final AuditRepository repo; public AuditService(AuditRepository repo){this.repo=repo;} public void record(String action,String details){repo.add(new AuditEvent(action,details));} public List<AuditEvent> getAuditLog(){return repo.findAll();} }
