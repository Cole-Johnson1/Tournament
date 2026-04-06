package tournament.domain.state;
import tournament.domain.Tournament;
public class ScheduledState extends AbstractTournamentState { public String getName(){return "Scheduled";} public void startTournament(Tournament t){ t.setState(new InProgressState()); } }
