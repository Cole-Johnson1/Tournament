package tournament.domain.factory;
import tournament.domain.*;
 import tournament.domain.policy.*;
 import tournament.domain.strategy.*;
public class TournamentFactory {
 private static final TournamentFactory INSTANCE = new TournamentFactory();
 private TournamentFactory() {}
 public static TournamentFactory getInstance() { return INSTANCE; }
 public Tournament createTournament(String name, TournamentType type){ return switch(type){ case ROUND_ROBIN -> new Tournament(name,type,new StandardRuleSetPolicy(),new FixedRosterSizePolicy(5),new StandardDisputeWindowPolicy(48),new RoundRobinScheduler()); case ELIMINATION -> new Tournament(name,type,new StandardRuleSetPolicy(),new FixedRosterSizePolicy(5),new StandardDisputeWindowPolicy(48),new EliminationScheduler()); }; }
}
