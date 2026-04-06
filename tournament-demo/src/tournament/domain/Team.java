package tournament.domain;
import java.util.*;
public class Team {
 private final String name; private final String captainName; private final List<Player> roster=new ArrayList<>();
 public Team(String name,String captainName){this.name=name.trim();this.captainName=captainName.trim();}
 public void addPlayer(Player player,int maxRosterSize){ if(roster.size()>=maxRosterSize) throw new IllegalStateException("RosterLimitExceeded"); if(roster.contains(player)) throw new IllegalStateException("DuplicatePlayer"); roster.add(player);}
 public boolean hasPlayerNamed(String playerName){ return roster.stream().anyMatch(p->p.getName().equalsIgnoreCase(playerName)); }
 public String getName(){return name;} public String getCaptainName(){return captainName;} public List<Player> getRoster(){return Collections.unmodifiableList(roster);}
}
