package tournament.infrastructure.auth;
import tournament.domain.Role;
public class AuthAdapter { private final ExternalAuthProvider provider; public AuthAdapter(ExternalAuthProvider provider){this.provider=provider;} public Role getRoleForUser(String userId){ String role=provider.fetchRole(userId); return switch(role){ case "organizer" -> Role.ORGANIZER; case "captain" -> Role.TEAM_CAPTAIN; case "referee" -> Role.REFEREE; case "admin" -> Role.SYSTEM_ADMINISTRATOR; default -> Role.PARTICIPANT; }; } }
