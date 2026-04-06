package tournament.domain.state;
import tournament.domain.Tournament;
public class DraftState extends AbstractTournamentState { public String getName(){return "Draft";} public void openRegistration(Tournament t){ t.setState(new RegistrationOpenState()); } public void cancel(Tournament t){ t.setState(new CancelledState()); } }
