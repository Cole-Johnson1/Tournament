package tournament.application.repository;

import tournament.domain.Tournament;

public interface TournamentRepository { void save(Tournament t); Tournament getCurrentTournament(); boolean hasTournament(); }
