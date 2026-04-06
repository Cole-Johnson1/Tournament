package tournament.domain;
public class MatchResult {
 private final int teamOneScore, teamTwoScore;
 public MatchResult(int a,int b){teamOneScore=a;teamTwoScore=b;}
 public boolean isDraw(){return teamOneScore==teamTwoScore;}
 public String getWinnerTeamName(Match match){ if(isDraw()) return null; return teamOneScore>teamTwoScore?match.getTeamOne().getName():match.getTeamTwo().getName(); }
 public int getTeamOneScore(){return teamOneScore;} public int getTeamTwoScore(){return teamTwoScore;}
}
