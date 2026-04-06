package tournament.domain.strategy;
import java.util.*; import tournament.domain.*;
public interface SchedulerStrategy { List<Match> generateSchedule(List<Team> teams); String getName(); }
