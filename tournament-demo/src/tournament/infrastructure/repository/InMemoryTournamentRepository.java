package tournament.infrastructure.repository;
import tournament.application.repository.TournamentRepository;
import tournament.domain.Tournament;
public class InMemoryTournamentRepository implements TournamentRepository { private Tournament current; public void save(Tournament t){current=t;} public Tournament getCurrentTournament(){return current;} public boolean hasTournament(){return current!=null;} }
