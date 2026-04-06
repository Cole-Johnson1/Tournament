package tournament.infrastructure.auth;
public class SimpleExternalAuthProvider implements ExternalAuthProvider { public String fetchRole(String userId){ return switch(userId.toLowerCase()){ case "organizer1" -> "organizer"; case "captain1","captain2","captain3","captain4" -> "captain"; case "ref1" -> "referee"; default -> "participant"; }; } }
