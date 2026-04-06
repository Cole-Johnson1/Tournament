package tournament.application;
import java.util.*; import tournament.domain.*; import tournament.infrastructure.repository.TournamentRepository;
public class StandingsService { private final TournamentRepository repo; public StandingsService(TournamentRepository repo){this.repo=repo;} public List<StandingsEntry> getStandings(){ return repo.getCurrentTournament().calculateStandings(); } }
