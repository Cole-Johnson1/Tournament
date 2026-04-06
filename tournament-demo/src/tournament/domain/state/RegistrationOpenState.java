package tournament.domain.state;
import tournament.domain.Tournament;
public class RegistrationOpenState extends AbstractTournamentState { public String getName(){return "RegistrationOpen";} public void closeRegistrationAndSeed(Tournament t){ t.setState(new SeededState()); } }
