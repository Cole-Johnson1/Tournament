package tournament.domain.state;
import tournament.domain.Tournament;
public class SeededState extends AbstractTournamentState { public String getName(){return "Seeded";} public void generateSchedule(Tournament t){ t.setState(new ScheduledState()); } }
