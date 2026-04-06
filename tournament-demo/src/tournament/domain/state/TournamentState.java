package tournament.domain.state;
import tournament.domain.Tournament;
public interface TournamentState { String getName(); void openRegistration(Tournament t); void closeRegistrationAndSeed(Tournament t); void generateSchedule(Tournament t); void startTournament(Tournament t); void finalizeTournament(Tournament t); }
