package tournament.domain.state;
import tournament.domain.Tournament;
public class InProgressState extends AbstractTournamentState { public String getName(){return "InProgress";} public void finalizeTournament(Tournament t){ t.setState(new CompleteState()); } }
