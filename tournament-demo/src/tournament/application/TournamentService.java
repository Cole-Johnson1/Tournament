package tournament.application;
import tournament.application.repository.TournamentRepository;
 import tournament.domain.*;
 import tournament.domain.factory.TournamentFactory;
 import tournament.infrastructure.auth.AuthAdapter;
public class TournamentService {
 private final TournamentRepository repo; private final TournamentFactory factory; private final AuthAdapter auth; private final AuditService audit;
 public TournamentService(TournamentRepository repo,TournamentFactory factory,AuthAdapter auth,AuditService audit){this.repo=repo;this.factory=factory;this.auth=auth;this.audit=audit;}
 private void require(Role expected,String userId){ if(auth.getRoleForUser(userId)!=expected) throw new SecurityException("AuthorizationFailed"); }
 public Tournament createTournament(String userId,String name,TournamentType type){ require(Role.ORGANIZER,userId); Tournament t=factory.createTournament(name,type); repo.save(t); audit.record("CreateTournament",name+" ("+type+")"); return t; }
 public Tournament getCurrentTournament(){ return repo.getCurrentTournament(); }
 public void openRegistration(String userId){ require(Role.ORGANIZER,userId); repo.getCurrentTournament().openRegistration(); audit.record("OpenRegistration","Registration opened"); }
 public void registerTeam(String userId,String teamName,String captainName){ require(Role.TEAM_CAPTAIN,userId); repo.getCurrentTournament().registerTeam(new Team(teamName,captainName)); audit.record("RegisterTeam",teamName); }
 public void addPlayerToTeam(String userId,String teamName,String playerName){ require(Role.TEAM_CAPTAIN,userId); repo.getCurrentTournament().addPlayerToTeam(teamName,new Player(playerName)); audit.record("AddPlayer",playerName+" -> "+teamName); }
 public void closeRegistrationAndSeed(String userId){ require(Role.ORGANIZER,userId); repo.getCurrentTournament().closeRegistrationAndSeed(); audit.record("CloseRegistrationAndSeed","Registration closed and teams seeded"); }
 public void generateSchedule(String userId){ require(Role.ORGANIZER,userId); repo.getCurrentTournament().generateSchedule(); audit.record("GenerateSchedule",repo.getCurrentTournament().getSchedulerStrategy().getName()); }
 public void startTournament(String userId){ require(Role.ORGANIZER,userId); repo.getCurrentTournament().startTournament(); audit.record("StartTournament","Tournament started"); }

 public void finalizeTournament(String userId){ require(Role.ORGANIZER,userId); repo.getCurrentTournament().finalizeTournament(); audit.record("FinalizeTournament","Tournament completed"); }

 public void cancelTournament(String userId){ require(Role.ORGANIZER,userId); repo.getCurrentTournament().cancel(); audit.record("CancelTournament","Tournament cancelled"); }

 public Tournament findByName(String name){ return repo.findByName(name); }
}
