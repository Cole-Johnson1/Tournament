package tournament.domain.strategy;
import java.util.*; import tournament.domain.*;
public class EliminationScheduler implements SchedulerStrategy {
 public List<Match> generateSchedule(List<Team> teams){ if(teams.size()<2) throw new IllegalStateException("Need at least 2 teams"); List<Match> matches=new ArrayList<>(); int id=1; for(int i=0;i+1<teams.size();i+=2) matches.add(new Match(id++, teams.get(i), teams.get(i+1))); return matches; }
 public String getName(){return "Elimination";}
}
