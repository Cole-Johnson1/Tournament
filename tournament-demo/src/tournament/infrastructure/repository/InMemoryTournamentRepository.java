package tournament.infrastructure.repository;
import tournament.application.repository.TournamentRepository;
import tournament.domain.Tournament;
public class InMemoryTournamentRepository implements TournamentRepository {
    private final java.util.Map<String, Tournament> store = new java.util.LinkedHashMap<>();
    private Tournament current;
    public void save(Tournament t) { current = t; store.put(t.getName(), t); }
    public Tournament getCurrentTournament() { return current; }
    public boolean hasTournament() { return current != null; }
    public Tournament findByName(String name) { return store.get(name); }
}
