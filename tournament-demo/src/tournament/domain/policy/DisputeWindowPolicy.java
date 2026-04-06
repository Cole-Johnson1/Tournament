package tournament.domain.policy;
import java.time.LocalDateTime;
public interface DisputeWindowPolicy { boolean isWithinWindow(LocalDateTime finalizedAt, LocalDateTime openedAt); }
