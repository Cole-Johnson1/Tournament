package tournament.domain.strategy;
import java.util.*; import tournament.domain.*;
public class RoundRobinScheduler implements SchedulerStrategy {
 public List<Match> generateSchedule(List<Team> teams){ List<Match> matches=new ArrayList<>(); int id=1; for(int i=0;i<teams.size();i++) for(int j=i+1;j<teams.size();j++) matches.add(new Match(id++, teams.get(i), teams.get(j))); return matches; }
 public String getName(){return "Round Robin";}
}
