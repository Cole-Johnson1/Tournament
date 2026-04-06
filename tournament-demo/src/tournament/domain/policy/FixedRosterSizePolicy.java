package tournament.domain.policy;
public class FixedRosterSizePolicy implements RosterSizePolicy { private final int max; public FixedRosterSizePolicy(int max){this.max=max;} public int getMaxRosterSize(){return max;} }
