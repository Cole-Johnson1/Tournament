package tournament.application;
import java.util.*;
 import tournament.application.repository.TournamentRepository;
 import tournament.domain.*;
public class StandingsService { private final TournamentRepository repo; public StandingsService(TournamentRepository repo){this.repo=repo;} public List<StandingsEntry> getStandings(){ return repo.getCurrentTournament().calculateStandings(); } }
