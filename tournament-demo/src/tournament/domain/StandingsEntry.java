package tournament.domain;
public class StandingsEntry {
 private final String teamName; private int wins,losses,draws,points;
 public StandingsEntry(String teamName){this.teamName=teamName;}
 public void recordWin(int p){wins++;points+=p;} public void recordLoss(){losses++;} public void recordDraw(int p){draws++;points+=p;}
 public String getTeamName(){return teamName;} public int getWins(){return wins;} public int getLosses(){return losses;} public int getDraws(){return draws;} public int getPoints(){return points;}
}
