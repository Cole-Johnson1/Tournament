package tournament.presentation;
import tournament.application.*;
 import tournament.domain.TournamentType;
public class DemoRunner { private final TournamentFacade facade; private final TournamentConsoleUI ui; public DemoRunner(TournamentFacade facade,TournamentConsoleUI ui){this.facade=facade;this.ui=ui;} public void runScriptedDemo(){ System.out.println("=== Scripted Tournament Demo ===\n");
 facade.createTournament("organizer1","Campus Showdown",TournamentType.ROUND_ROBIN); facade.openRegistration("organizer1"); facade.registerTeam("captain1","Falcons","Alice"); facade.registerTeam("captain2","Wolves","Bob"); facade.registerTeam("captain3","Titans","Carol"); facade.registerTeam("captain4","Sharks","Dave"); facade.addPlayerToTeam("captain1","Falcons","F1"); facade.addPlayerToTeam("captain2","Wolves","W1"); facade.addPlayerToTeam("captain3","Titans","T1"); facade.addPlayerToTeam("captain4","Sharks","S1"); facade.closeRegistrationAndSeed("organizer1"); facade.generateSchedule("organizer1"); facade.startTournament("organizer1");
 facade.submitResult("ref1",1,2,1); facade.submitResult("ref1",2,0,0); facade.submitResult("ref1",3,3,1); facade.submitResult("ref1",4,1,2); facade.submitResult("ref1",5,1,0); facade.submitResult("ref1",6,2,2);

 ui.showTournamentSummary(facade.getCurrentTournament()); ui.showStandings(facade.getStandings());

 System.out.println("\n--- Dispute Workflow ---"); facade.openDispute("captain1",1,"Possible score entry issue"); ui.showDisputes(facade.getDisputes()); facade.resolveDispute("organizer1",1,true,1,1); System.out.println("Dispute resolved - score corrected to 1:1"); ui.showDisputes(facade.getDisputes()); ui.showStandings(facade.getStandings());

 facade.finalizeTournament("organizer1"); System.out.println("\nTournament state: "+facade.getCurrentTournament().getState().getName());

 System.out.println("\n--- Error Handling Demo ---");
 try { facade.registerTeam("captain1","NewTeam","Test"); } catch(Exception e){ System.out.println("Expected error (register after complete): "+e.getMessage()); }
 try { facade.cancelTournament("organizer1"); } catch(Exception e){ System.out.println("Expected error (cancel completed): "+e.getMessage()); }

 ui.showAuditLog();
 System.out.println("\n=== Demo Complete ==="); } }
