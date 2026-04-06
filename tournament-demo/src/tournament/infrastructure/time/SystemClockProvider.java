package tournament.infrastructure.time;

import java.time.LocalDateTime;

public class SystemClockProvider {
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
